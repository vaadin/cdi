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

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.ui.UI;

/**
 * UIScopedContext is the context for
 *
 * @VaadinUIScoped beans.
 */
public class UIScopedContext implements Context {

    private final BeanManager beanManager;

    public UIScopedContext(final BeanManager beanManager) {
        getLogger().info("Instantiating UIScoped context");
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUIScoped.class;
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

        getLogger().log(Level.INFO,
                "Getting bean for contextual {0} and creational context {1}",
                new Object[]{contextual, creationalContext});

        BeanStoreContainer beanStoreContainer = getSessionBoundBeanStoreContainer();
        T beanInstance = null;
        int uiId;
        UIBeanStore beanStore;

        if (isInstanceOfUIBean(contextual)) {
            UIBean uiBean = (UIBean) contextual;
            uiId = uiBean.getUiId();
            beanStore = beanStoreContainer.getOrCreateUIBeanStoreFor(uiBean);
            beanInstance = beanStore.getBeanInstance(contextual,
                    creationalContext);
            if (beanStoreContainer.isBeanStoreCreationPending()) {
                beanStoreContainer.assignPendingBeanStoreFor((UI) beanInstance,
                        uiId);
            }
            /**
             * In case of a CDI event listener, the Contextual is NOT a UIBean,
             * rather than just a Bean.
             */
        } else if (isUIBean(contextual)) {
            final UI current = UI.getCurrent();
            if (current == null) {
                throw new IllegalStateException(
                        "CDI listener identified, but there is no active UI available.");
            }
            Bean<T> bean = (Bean<T>) contextual;
            if (bean.getBeanClass().isAssignableFrom(current.getClass())) {
                beanInstance = (T) current;
            } else if (creationalContext != null) {
                getLogger()
                        .log(Level.WARNING,
                        "Tried to get a Bean that is not compatible with the current UI {0}. "
                        + "Looks like you need to specify \"notifyObserver=Reception.IF_EXISTS\" on the event observer methods of {1}.",
                        new Object[]{current,
                    bean.getBeanClass().getName()});
            }
        } else {
            throw new IllegalStateException(((Bean) contextual).getBeanClass()
                    .getName()
                    + " is not a UI, only UIs can be annotated with @VaadinUI!");
        }

        getLogger()
                .log(Level.INFO,
                "Finished getting bean for contextual {0}, returning instance {1}",
                new Object[]{contextual, beanInstance});
        return beanInstance;
    }

    /**
     * @param contextual
     * @return true if Vaadin UI is assignabled from given bean's representing
     * type
     */
    private <T> boolean isInstanceOfUIBean(Contextual<T> contextual) {
        if (contextual instanceof UIBean) {
            return UI.class.isAssignableFrom(((Bean<T>) contextual)
                    .getBeanClass());
        }

        return false;
    }

    private <T> boolean isUIBean(Contextual<T> contextual) {
        if (contextual instanceof Bean) {
            return UI.class.isAssignableFrom(((Bean<T>) contextual)
                    .getBeanClass());
        }

        return false;
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
