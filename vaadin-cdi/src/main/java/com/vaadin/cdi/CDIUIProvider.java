package com.vaadin.cdi;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
        Integer uiId = uiCreateEvent.getUiId();
        VaadinRequest request = uiCreateEvent.getRequest();
        Bean<?> uiBean = getUIBeanMatchingDeploymentDescriptor(type);

        if (uiBean == null) {
            if (type.isAnnotationPresent(VaadinUI.class)) {
                String uiMapping = parseUIMapping(request);
                uiBean = getUIBeanMatchingQualifierMapping(uiMapping);
            }
        }
        if (uiBean != null) {
            UI ui = (UI) beanManager.getReference(uiBean, type,
                    beanManager.createCreationalContext(uiBean));
            return ui;
        }
        throw new IllegalStateException("Could not instantiate UI");
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent selectionEvent) {
        VaadinRequest request = selectionEvent.getRequest();
        String uiMapping = parseUIMapping(request);
        if (isRoot(request)) {
            return rootUI();
        }
        Bean<?> uiBean = getUIBeanMatchingQualifierMapping(uiMapping);

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
        String pathInfo = request.getRequestPathInfo();
        String contextPath = request.getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        if(request.getRequestPathInfo() == null)
            return false;
        return pathInfo.endsWith(contextPath);
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

    private Bean<?> getUIBeanMatchingQualifierMapping(String mapping) {
        Set<Bean<?>> beans = beanManager.getBeans(UI.class,
               new AnnotationLiteral<Any>() {});

        for (Bean<?> bean : beans) {
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

        return null;
    }

    private Bean<?> getUIBeanMatchingDeploymentDescriptor(
            Class<? extends UI> type) {

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
        return parseUIMapping(request.getRequestPathInfo());
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
