/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.cdi.internal;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.vaadin.cdi.Root;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class CDIUIProvider extends DefaultUIProvider implements Serializable {

    @Inject
    private BeanManager beanManager;

    @Override
    public UI createInstance(UICreateEvent uiCreateEvent) {
        Class<? extends UI> type = uiCreateEvent.getUIClass();
        int uiId = uiCreateEvent.getUiId();
        VaadinRequest request = uiCreateEvent.getRequest();
        Bean<?> bean = scanForBeans(type);
        String uiMapping = "";
        if (bean == null) {
            if (type.isAnnotationPresent(VaadinUI.class)) {
                uiMapping = parseUIMapping(request);
                bean = getUIBeanWithMapping(uiMapping);
            } else {
                throw new IllegalStateException("UI class: " + type.getName()
                        + " with mapping: " + uiMapping
                        + " is not annotated with VaadinUI!");
            }
        }
        UIBean uiBean = new UIBean(bean, uiId);
        return (UI) beanManager.getReference(uiBean, type,
                beanManager.createCreationalContext(bean));
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent selectionEvent) {
        VaadinRequest request = selectionEvent.getRequest();
        String uiMapping = parseUIMapping(request);
        if (isRoot(request)) {
            return rootUI();
        }
        Bean<?> uiBean = getUIBeanWithMapping(uiMapping);

        if (uiBean != null) {
            return uiBean.getBeanClass().asSubclass(UI.class);
        }

        if (uiMapping.isEmpty()) {
            // See if UI is configured to web.xml with VaadinCDIServlet. This is
            // done only if no specific UI name is given.
            return super.getUIClass(selectionEvent);
        }

        return null;
    }

    boolean isRoot(VaadinRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            return false;
        }

        return pathInfo.equals("/");
    }

    Class<? extends UI> rootUI() {
        Set<Bean<?>> beans = beanManager.getBeans(UI.class,
                new AnnotationLiteral<Root>() {
                });
        if (beans.isEmpty()) {
            return null;
        }
        if (beans.size() > 1) {
            StringBuilder errorMessage = new StringBuilder();
            for (Bean<?> bean : beans) {
                errorMessage.append(bean.getBeanClass().getName());
                errorMessage.append("/n");
            }
            throw new IllegalStateException(
                    "Multiple beans are annotated with @Root: "
                            + errorMessage.toString());
        }
        Bean<?> uiBean = beans.iterator().next();
        Class<?> rootUI = uiBean.getBeanClass();
        return rootUI.asSubclass(UI.class);
    }

    private Bean<?> getUIBeanWithMapping(String mapping) {
        Set<Bean<?>> beans = beanManager.getBeans(UI.class,
                new AnnotationLiteral<Any>() {
                });

        for (Bean<?> bean : beans) {
            // We need this check since the returned beans can also be producers
            if (UI.class.isAssignableFrom(bean.getBeanClass())) {
                Class<? extends UI> beanClass = bean.getBeanClass().asSubclass(
                        UI.class);

                if (beanClass.isAnnotationPresent(VaadinUI.class)) {
                    String computedMapping = Conventions
                            .deriveMappingForUI(beanClass);
                    if (mapping.equals(computedMapping)) {
                        return bean;
                    }
                }
            }
        }

        return null;
    }

    private Bean<?> scanForBeans(Class<? extends UI> type) {

        Set<Bean<?>> beans = beanManager.getBeans(type,
                new AnnotationLiteral<Any>() {
                });

        if (beans.isEmpty()) {
            getLogger().warning(
                    "Could not find UI bean for " + type.getCanonicalName());
            return null;
        }

        if (beans.size() > 1) {
            getLogger().warning(
                    "Found multiple UI beans for " + type.getCanonicalName());
            return null;
        }

        return beans.iterator().next();
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
                int lastIndex = path.lastIndexOf('/');
                return path.substring(lastIndex + 1);
            } else {
                int lastIndexOfBang = path.lastIndexOf('!');
                // strip slash with bank => /!
                String pathWithoutView = path.substring(0, lastIndexOfBang - 1);
                int lastSlashIndex = pathWithoutView.lastIndexOf('/');
                return pathWithoutView.substring(lastSlashIndex + 1);
            }
        }
        return "";
    }

    private static Logger getLogger() {
        return Logger.getLogger(CDIUIProvider.class.getCanonicalName());
    }
}
