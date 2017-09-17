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
 *
 */

package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Manage and store ContextualStorage for view context.
 * This class is responsible for
 * - selecting the active view context
 * - creating, and providing the ContextualStorage for it
 * - destroying contextual instances
 *
 * Concurrency handling ignored intentionally.
 * Locking of VaadinSession is the responsibility of Vaadin Framework.
 */
@NormalUIScoped
public class ViewContextualStorageManager implements Serializable {
    private final static Storage CLOSED = new ClosedStorage();
    private Storage openingContext = CLOSED;
    private Storage currentContext = CLOSED;
    @Inject
    private BeanManager beanManager;
    @Inject
    private ViewContextStrategyProvider viewContextStrategyManager;

    public void applyChange(ViewChangeListener.ViewChangeEvent event) {
        if (!currentContext.contains(event.getViewName(), event.getParameters())) {
            currentContext.destroy();
            currentContext = openingContext;
            openingContext = CLOSED;
        }
    }

    public View prepareChange(Bean viewBean, String viewName, String parameters) {
        final Class beanClass = viewBean.getBeanClass();
        final Storage temp = currentContext;
        if (!currentContext.contains(viewName, parameters)) {
            openingContext.destroy();
            ViewContextStrategy strategy = viewContextStrategyManager.lookupStrategy(beanClass);
            openingContext = new Storage(strategy);
            currentContext = openingContext;
        }
        final View view = (View) BeanProvider.getContextualReference(beanClass, viewBean);
        currentContext = temp;
        return view;
    }

    public void revertChange(ViewChangeListener.ViewChangeEvent event) {
        if (openingContext.contains(event.getViewName(), event.getParameters())) {
            openingContext.destroy();
            openingContext = CLOSED;
        }
    }

    public ContextualStorage getContextualStorage(boolean createIfNotExist) {
        return currentContext.getContextualStorage(beanManager, createIfNotExist);
    }

    public boolean isActive() {
        return currentContext != CLOSED;
    }

    @PreDestroy
    private void preDestroy() {
        openingContext.destroy();
        currentContext.destroy();
    }

    private static class Storage implements Serializable {
        ContextualStorage contextualStorage;
        final ViewContextStrategy strategy;

        Storage(ViewContextStrategy strategy) {
            this.strategy = strategy;
        }

        ContextualStorage getContextualStorage(BeanManager beanManager, boolean createIfNotExist) {
            if (createIfNotExist && contextualStorage == null) {
                contextualStorage = new VaadinContextualStorage(beanManager);
            }
            return contextualStorage;
        }

        void destroy() {
            if (contextualStorage != null) {
                AbstractContext.destroyAllActive(contextualStorage);
            }
        }

        boolean contains(String viewName, String parameters) {
            return strategy.contains(viewName, parameters);
        }

    }

    private static class ClosedStorage extends Storage {
        ClosedStorage() {
            super((viewName, parameters) -> false);
        }

        @Override
        ContextualStorage getContextualStorage(BeanManager beanManager, boolean createIfNotExist) {
            throw new IllegalStateException("Storage is closed");
        }
    }

}
