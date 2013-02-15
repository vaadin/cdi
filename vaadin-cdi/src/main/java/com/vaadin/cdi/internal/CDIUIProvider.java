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

import com.vaadin.cdi.Root;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * A Vaadin UI Provider that returns CDI-managed {@link UI}s. This class is not
 * part of the public API and should not be used by clients directly.
 *
 * @see UIProvider
 */
public class CDIUIProvider extends DefaultUIProvider implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(CDIUIProvider.class.getCanonicalName());
    @Inject
    private BeanManager beanManager;

    @Override
    public UI createInstance(UICreateEvent uiCreateEvent) {
        Class<? extends UI> type = uiCreateEvent.getUIClass();
        int uiId = uiCreateEvent.getUiId();
        
        logger.log(Level.INFO, "Creating new UI instance of type {0} with ID {1}", 
                new Object[]{type.getCanonicalName(), uiId});
        
        VaadinRequest request = uiCreateEvent.getRequest();
        Bean<?> bean = scanForUIBeans(type);
        String uiMapping = "";
        if (bean == null) {
            if (type.isAnnotationPresent(VaadinUI.class)) {
                uiMapping = Conventions.deriveUIMappingFromRequest(request);
                bean = getUIBeanWithMapping(uiMapping);
            } else {
                throw new IllegalStateException("UI class: " + type.getName()
                        + " with mapping: " + uiMapping
                        + " is not annotated with VaadinUI!");
            }
        }
        VaadinUIBean uiBean = new VaadinUIBean(bean, uiId);
        return (UI) beanManager.getReference(uiBean, type,
                beanManager.createCreationalContext(bean));
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent selectionEvent) {
        VaadinRequest request = selectionEvent.getRequest();
        String uiMapping = Conventions.deriveUIMappingFromRequest(request);
        if (isRoot(request)) {
            return rootUI();
        }
        Bean<?> uiBean = getUIBeanWithMapping(uiMapping);

        if (uiBean != null) {
            return uiBean.getBeanClass().asSubclass(UI.class);
        }

        if (uiMapping.isEmpty()) {
            // See if UI is configured as a servlet parameter. This is
            // done only if no specific UI name is given.
            return super.getUIClass(selectionEvent);
        }

        return null;
    }

    private static boolean isRoot(VaadinRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            return false;
        }

        return pathInfo.equals("/");
    }

    private Class<? extends UI> rootUI() {
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

    private Bean<?> scanForUIBeans(Class<? extends UI> type) {
        Set<Bean<?>> beans = beanManager.getBeans(type,
                new AnnotationLiteral<Any>() {
                });

        if (beans.isEmpty()) {
            logger.log(Level.WARNING, "Could not find UI bean for {0}", type.getCanonicalName());
            return null;
        }

        if (beans.size() > 1) {
            logger.log(Level.WARNING, "Found multiple UI beans for {0}", type.getCanonicalName());
            return null;
        }

        return beans.iterator().next();
    }
}
