/*
 * Copyright 2013 Vaadin Ltd.
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

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.UI;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Context for {@link UIScoped} beans. This class is not part of the public API
 * and should not be used by clients directly.
 */
public class UIScopedContext implements Context {

    private static final Logger logger = Logger.getLogger(UIScopedContext.class.getCanonicalName());
    private final BeanManager beanManager;

    public UIScopedContext(BeanManager beanManager) {
        logger.log(Level.INFO, "Instantiating {0}", UIScopedContext.class.getSimpleName());
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        if (!isActive()) {
            throw new ContextNotActiveException("There is no active UI available");
        }

        logger.log(Level.INFO,
                "Getting bean for contextual {0} and creational context {1}",
                new Object[]{contextual, creationalContext});

        final T instance = getActiveBeanStore().getBeanInstance(contextual, creationalContext);

        logger.log(Level.FINE, "Finished getting bean for contextual {0}, returning instance {1}",
                new Object[]{contextual, instance});

        return instance;
    }

    private BeanStore getActiveBeanStore() {
        // When getActiveBeanStore() is called, a check should already have been made
        // to verify that there is in fact a current UI
        final UI current = UI.getCurrent();
        final int uiId = current.getUIId();
        final Set<Bean<?>> uiBeans = beanManager.getBeans(current.getClass());
        
        if (uiBeans.isEmpty()) {
            throw new IllegalStateException("No bean of type " + current.getClass().getCanonicalName() + " was found");
        }
        
        if (uiBeans.size() > 1) {
            throw new IllegalStateException("More than one bean of type " + current.getClass().getCanonicalName() + " was found");
        }
        
        final Bean<?> uiBean = uiBeans.iterator().next();        
        return getSessionBoundBeanStoreContainer().getOrCreateUIBeanStoreFor(new VaadinUIBean(uiBean, uiId));
    }

    private UIBeanStoreContainer getSessionBoundBeanStoreContainer() {
        Set<Bean<?>> beans = beanManager.getBeans(UIBeanStoreContainer.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "No bean store container bound to session");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean store container bound to session");
        }

        Bean<?> bean = beans.iterator().next();
        return (UIBeanStoreContainer) beanManager.getReference(bean,
                bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public boolean isActive() {
        return UI.getCurrent() != null;
    }
}
