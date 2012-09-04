package com.vaadin.cdi;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.vaadin.Application;
import com.vaadin.cdi.VaadinContext.BeanStoreContainer;
import com.vaadin.server.UIProvider;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

public class CDIUIProvider implements UIProvider {

    @Inject
    private BeanManager beanManager;

    @Inject
    private BeanStoreContainer beanStoreContainer;

    @Override
    public UI createInstance(Application application, Class<? extends UI> type,
            WrappedRequest request) {
        String UIMapping = parseUIMapping(request);
        Bean<?> uiBean = getUIBeanMatchingMapping(UIMapping);

        if (uiBean != null) {

            System.out.println("Instantiating new UI from CDIUIProvider");
            UI ui = (UI) beanManager.getReference(uiBean, type,
                    beanManager.createCreationalContext(uiBean));
            beanStoreContainer.uiInitialized(ui);
            ui.setApplication(application);

            System.out.println(ui);

            return ui;
        }

        throw new RuntimeException("Could not instantiate UI");
    }

    @Override
    public Class<? extends UI> getUIClass(Application application,
            WrappedRequest request) {
        String UIMapping = parseUIMapping(request);
        Bean<?> uiBean = getUIBeanMatchingMapping(UIMapping);

        if (uiBean != null) {
            return uiBean.getBeanClass().asSubclass(UI.class);
        }

        return null;
    }

    private Bean<?> getUIBeanMatchingMapping(String mapping) {
        Set<Bean<?>> beans = beanManager.getBeans(UI.class,
                new VaadinUIAnnotation());

        for (Bean<?> bean : beans) {
            Class<? extends UI> beanClass = bean.getBeanClass().asSubclass(
                    UI.class);

            if (beanClass.isAnnotationPresent(VaadinUI.class)) {
                VaadinUI annotation = beanClass.getAnnotation(VaadinUI.class);

                if (annotation.mapping() != null) {
                    if (mapping.equals(annotation.mapping())) {
                        return bean;
                    }
                }
            }
        }

        return null;
    }

    private String parseUIMapping(WrappedRequest request) {
        String requestPath = request.getRequestPathInfo();
        if (requestPath != null && requestPath.length() > 1) {
            if (requestPath.endsWith("/")) {
                return requestPath.substring(1, requestPath.lastIndexOf("/"));
            } else {
                return requestPath.substring(1);
            }
        }
        return "";
    }

}
