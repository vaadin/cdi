/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.vaadin.cdi.internal.AnnotationUtil;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.UIContextualStorageManager;
import com.vaadin.cdi.internal.VaadinSessionScopedContext;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;

@ApplicationScoped
public class CDIUIProvider extends DefaultUIProvider {

    private static final String PATH_WILDCARD = "/*";

    @Inject
    private UIContextualStorageManager uiContextualStorageManager;

    @Inject
    private BeanManager beanManager;

    private final DetachListener detachListener = new DetachListenerImpl();

    private static final Annotation QUALIFIER_ANY = new AnnotationLiteral<Any>() {
    };

    public final class DetachListenerImpl implements DetachListener {

        @Override
        public void detach(DetachEvent event) {
            Object source = event.getSource();
            if (source instanceof UI) {
                int uiId = ((UI) source).getUIId();
                if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
                    // Happens on tomcat when it expires sessions upon undeploy.
                    // We would get ContextNotActiveException on
                    // uiContextualStorageManager.destroy
                    getLogger().log(Level.WARNING,
                            "VaadinSessionScoped context does not exist. "
                                    + "Maybe application is undeployed."
                                    + " Can''t destroy UI context for UI {0}.",
                            uiId);
                    return;
                }
                uiContextualStorageManager.destroy(uiId);
            }
        }
    }

    @Override
    public UI createInstance(UICreateEvent uiCreateEvent) {
        getLogger().fine("Creating new UI instance");
        Class<? extends UI> type = uiCreateEvent.getUIClass();
        int uiId = uiCreateEvent.getUiId();
        VaadinRequest request = uiCreateEvent.getRequest();
        Bean<?> bean = scanForBeans(type, request);
        try {
            // Make the UI id available to UIScopedContext when creating nested
            // injected objects
            uiContextualStorageManager.prepareOpening(uiId);
            UI ui = (UI) beanManager.getReference(bean, type,
                    beanManager.createCreationalContext(bean));
            ui.addDetachListener(detachListener);
            return ui;
        } finally {
            uiContextualStorageManager.cleanupOpening();
        }
    }

    @Override
    public Class<? extends UI> getUIClass(
            UIClassSelectionEvent selectionEvent) {
        VaadinRequest request = selectionEvent.getRequest();
        String uiMapping = parseUIMapping(request);

        Class<? extends UI> uiClass = null;
        String pathInfo = request.getContextPath() + "/";

        if (isRoot(request)) {
            uiClass = rootUI();
        } else {
            Bean<?> uiBean = getUIBeanWithMapping(uiMapping);

            if (uiBean != null) {
                // Provide correct path info for UI for push state navigation
                uiClass = uiBean.getBeanClass().asSubclass(UI.class);
                pathInfo += removeWildcard(
                        Conventions.deriveMappingForUI(uiClass));
            }
        }

        if (uiClass == null && uiMapping.isEmpty()) {
            // See if UI is configured to web.xml with VaadinCDIServlet. This is
            // done only if no specific UI name is given.
            uiClass = super.getUIClass(selectionEvent);
        }

        request.setAttribute(ApplicationConstants.UI_ROOT_PATH,
                (pathInfo.startsWith("/") ? "" : "/") + pathInfo);
        return uiClass;
    }

    boolean isRoot(VaadinRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            return false;
        }

        return pathInfo.equals("/") || pathInfo.startsWith("/!");
    }

    Class<? extends UI> rootUI() {
        Set<Bean<?>> rootBeans = AnnotationUtil.getRootUiBeans(beanManager);
        if (rootBeans.isEmpty()) {
            return null;
        }
        if (rootBeans.size() > 1) {
            StringBuilder errorMessage = new StringBuilder();
            for (Bean<?> bean : rootBeans) {
                errorMessage.append(bean.getBeanClass().getName());
                errorMessage.append("\n");
            }
            throw new IllegalStateException(
                    "Multiple beans are annotated with @CDIUI without context path: "
                            + errorMessage.toString());
        }
        Bean<?> uiBean = rootBeans.iterator().next();
        Class<?> rootUI = uiBean.getBeanClass();
        return rootUI.asSubclass(UI.class);
    }

    Bean<?> getUIBeanWithMapping(String mapping) {
        Set<Bean<?>> beans = AnnotationUtil.getUiBeans(beanManager);

        return beans.stream()
                .filter(bean -> UI.class.isAssignableFrom(bean.getBeanClass()))
                .filter(bean -> {
                    Class<? extends UI> beanClass = bean.getBeanClass()
                            .asSubclass(UI.class);
                    return beanClass.isAnnotationPresent(CDIUI.class)
                            && isMatchingPath(mapping, beanClass);
                }).sorted(Comparator.comparing(bean -> {
                    Class<?> beanClass = ((Bean<?>) bean).getBeanClass();
                    String path = Conventions.deriveMappingForUI(beanClass);
                    return removeWildcard(path).length();
                }).reversed()).findFirst().orElse(null);
    }

    private boolean isMatchingPath(String mapping,
            Class<? extends UI> beanClass) {
        String path = Conventions.deriveMappingForUI(beanClass);

        boolean pathEndsWithWildcard = path.endsWith(PATH_WILDCARD);
        boolean isWildcardPath = pathEndsWithWildcard
                || beanClass.isAnnotationPresent(PushStateNavigation.class);

        path = removeWildcard(path);

        boolean exactMatch = mapping.equals(path);
        if (!exactMatch && isWildcardPath) {
            return path.isEmpty() || mapping.startsWith(path + "/");
        }
        return exactMatch;
    }

    private String removeWildcard(String path) {
        if (path.endsWith(PATH_WILDCARD)) {
            return path.substring(0, path.length() - PATH_WILDCARD.length());
        }
        return path;
    }

    private Bean<?> scanForBeans(Class<? extends UI> type,
            VaadinRequest request) {
        Bean<?> bean = null;
        Set<Bean<?>> beans = beanManager.getBeans(type, QUALIFIER_ANY);

        if (beans.isEmpty()) {
            getLogger().warning(
                    "Could not find UI bean for " + type.getCanonicalName());
            return null;
        } else {
            try {
                bean = beanManager.resolve(beans);
            } catch (AmbiguousResolutionException e) {
                bean = null;
            }
        }

        String uiMapping = "";
        if (bean == null) {
            if (type.isAnnotationPresent(CDIUI.class)) {
                uiMapping = parseUIMapping(request);
                bean = getUIBeanWithMapping(uiMapping);
            } else {
                throw new IllegalStateException(
                        "UI class: " + type.getName() + " with mapping: "
                                + uiMapping + " is not annotated with CDIUI!");
            }
        }
        return bean;
    }

    String parseUIMapping(VaadinRequest request) {
        return parseUIMapping(request.getPathInfo());
    }

    String parseUIMapping(String requestPath) {
        if (requestPath != null && requestPath.length() > 1) {
            String path = requestPath;
            if (requestPath.endsWith("/")) {
                path = requestPath.substring(0, requestPath.length() - 1);
            }
            if (!path.contains("!")) {
                return path.substring(path.startsWith("/") ? 1 : 0);
            } else {
                int lastIndexOfBang = path.lastIndexOf('!');
                String pathWithoutView = path.substring(0, lastIndexOfBang);
                if (pathWithoutView.endsWith("/")) {
                    pathWithoutView = pathWithoutView.substring(0,
                            pathWithoutView.length() - 1);
                }
                return pathWithoutView
                        .substring(pathWithoutView.startsWith("/") ? 1 : 0);
            }
        }
        return "";
    }

    private static Logger getLogger() {
        return Logger.getLogger(CDIUIProvider.class.getCanonicalName());
    }
}
