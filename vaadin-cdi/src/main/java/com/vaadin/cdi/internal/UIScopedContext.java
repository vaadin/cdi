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
package com.vaadin.cdi.internal;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * UIScopedContext is the context for
 * 
 * @UIScoped beans.
 */
public class UIScopedContext implements Context {

    private final BeanManager beanManager;

    public UIScopedContext(final BeanManager beanManager) {
        getLogger().fine("Instantiating UIScoped context");
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        getLogger().log(Level.FINE,
                "Getting bean for contextual {0} and creational context {1}",
                new Object[] { contextual, creationalContext });

        BeanStoreContainer beanStoreContainer = getSessionBoundBeanStoreContainer();
        T beanInstance = null;

        UIBeanStore beanStore = null;

        // Get the correct UIBeanStore, or bean instance if available

        if (CurrentInstance.get(UIBean.class) != null) {
            // Not necessarily a UI but can be some other class in UI scope.
            // When creating a UI and the instances injected (directly or
            // indirectly) to it, the UI instance might not be fully constructed
            // yet but we have the UIBean.
            final UIBean uiBean = CurrentInstance.get(UIBean.class);

            beanStore = beanStoreContainer.getOrCreateUIBeanStoreFor(uiBean);
        } else if (UI.getCurrent() != null) {
            if (contextual instanceof Bean
                    && UI.class.isAssignableFrom(((Bean) contextual)
                            .getBeanClass())
                    && ((Bean) contextual).getBeanClass().isAssignableFrom(
                            UI.getCurrent().getClass())) {
                // for CDI events etc.
                // TODO ideally, this branch should not be needed
                beanInstance = (T) UI.getCurrent();
            } else {
                int uiId = UI.getCurrent().getUIId();

                beanStore = beanStoreContainer.getUIBeanStore(uiId);
            }
        } else {
            throw new IllegalStateException(
                    "CDI listener identified, but there is no active UI available.");
        }

        if (beanInstance == null && beanStore != null) {
            beanInstance = beanStore.getBeanInstance(contextual,
                    creationalContext);
        }

        getLogger()
                .log(Level.FINE,
                        "Finished getting bean for contextual {0}, returning instance {1}",
                        new Object[] { contextual, beanInstance });
        return beanInstance;
    }

    /**
     * @return bean store container bound to the user's http session
     */
    private BeanStoreContainer getSessionBoundBeanStoreContainer() {
        Set<Bean<?>> beans = beanManager.getBeans(BeanStoreContainer.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "No bean store container bound for session");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean store container available for session");
        }

        Bean<?> bean = beans.iterator().next();

        return (BeanStoreContainer) beanManager.getReference(bean,
                bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}
