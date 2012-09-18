package com.vaadin.cdi;

import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

public class CDIUIProvider extends DefaultUIProvider {

    @Inject
    private BeanManager beanManager;

    @Override
    public UI createInstance(WrappedRequest request, Class<? extends UI> type) {
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
    public Class<? extends UI> getUIClass(WrappedRequest request) {
        String uiMapping = parseUIMapping(request);

        Bean<?> uiBean = getUIBeanMatchingQualifierMapping(uiMapping);

        if (uiBean != null) {
            return uiBean.getBeanClass().asSubclass(UI.class);
        }

        if (uiMapping.isEmpty()) {
            // See if UI is configured to web.xml with VaadinCDIServlet. This is
            // done only if no specific UI name is given.
            return super.getUIClass(request);
        }

        return null;
    }

    private Bean<?> getUIBeanMatchingQualifierMapping(String mapping) {
        Set<Bean<?>> beans = beanManager.getBeans(UI.class,
                new VaadinUIAnnotation());

        for (Bean<?> bean : beans) {
            Class<? extends UI> beanClass = bean.getBeanClass().asSubclass(
                    UI.class);

            if (beanClass.isAnnotationPresent(VaadinUI.class)) {
                VaadinUI annotation = beanClass.getAnnotation(VaadinUI.class);

                if (annotation.mapping() != null
                        && !annotation.mapping().isEmpty()) {
                    if (mapping.equals(annotation.mapping())) {
                        return bean;
                    }
                } else {
                    String defaultMapping = Naming.firstToLower(beanClass
                            .getSimpleName());
                    if (mapping.equals(defaultMapping)) {
                        return bean;
                    }
                }
            }
        }

        return null;
    }

    private Bean<?> getUIBeanMatchingDeploymentDescriptor(
            Class<? extends UI> type) {

        // If @VaadinUI qualifier is not given but UI is defined in deployment
        // descriptor
        Set<Bean<?>> beans = beanManager.getBeans(type,
                new AnnotationLiteral<Any>() {
                });

        if (beans.isEmpty()) {
            //TODO: superfluous: ANY means everything
            // Otherwise check whether UI with qualifier exists
            beans = beanManager.getBeans(type, new VaadinUIAnnotation());
        }

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

    String parseUIMapping(WrappedRequest request) {
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
