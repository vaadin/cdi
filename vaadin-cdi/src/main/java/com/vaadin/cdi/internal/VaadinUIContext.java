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

import com.vaadin.cdi.VaadinUI;
import com.vaadin.ui.UI;
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Context for {@link VaadinUI} beans. This class is not part of the public API
 * and should not be used by clients directly.s
 */
public class VaadinUIContext implements Context {

    private static final Logger logger = Logger.getLogger(VaadinUIContext.class.getCanonicalName());
    private final BeanManager beanManager;

    public VaadinUIContext(final BeanManager beanManager) {
        logger.log(Level.INFO, "Instantiating {0}", VaadinUIContext.class.getSimpleName());
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUI.class;
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

        logger.log(Level.INFO,
                "Getting bean for contextual {0} and creational context {1}",
                new Object[]{contextual, creationalContext});

        final UIBeanStoreContainer beanStoreContainer = new BeanManagerUtil(beanManager).getSessionBoundBeanStoreContainer();
        T beanInstance = null;
        int uiId;
        BeanStore beanStore;

        if (isInstanceOfVaadinUIBean(contextual)) {
            final VaadinUIBean uiBean = (VaadinUIBean) contextual;
            uiId = uiBean.getUiId();
            beanStore = beanStoreContainer.getOrCreateUIBeanStoreFor(uiBean);
            beanInstance = beanStore.getBeanInstance(contextual,
                    creationalContext);
            if (beanStoreContainer.isBeanStoreCreationPending()) {
                beanStoreContainer.assignPendingBeanStoreFor((UI) beanInstance,
                        uiId);
            }
        } else if (isUIBean(contextual)) {
            final UI current = UI.getCurrent();
            if (current == null) {
                throw new IllegalStateException(
                        "Requested instance of current UI, but there is no active UI available");
            }
            Bean<T> bean = (Bean<T>) contextual;
            if (bean.getBeanClass().isAssignableFrom(current.getClass())) {
                beanInstance = (T) current;
            } else if (creationalContext != null) {
                logger.log(Level.WARNING,
                        "Tried to get a Bean that is not compatible with the current UI {0}. "
                        + "Looks like you need to specify \"notifyObserver=Reception.IF_EXISTS\" on the event observer methods of {1}.",
                        new Object[]{current, bean.getBeanClass().getName()});
            }
        } else {
            throw new IllegalStateException(((Bean) contextual).getBeanClass()
                    .getName()
                    + " is not a UI, only UIs can be annotated with @VaadinUI");
        }

        logger.log(Level.INFO,
                "Finished getting bean for contextual {0}, returning instance {1}",
                new Object[]{contextual, beanInstance});
        return beanInstance;
    }

    private <T> boolean isInstanceOfVaadinUIBean(Contextual<T> contextual) {
        if (contextual instanceof VaadinUIBean) {
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
}
