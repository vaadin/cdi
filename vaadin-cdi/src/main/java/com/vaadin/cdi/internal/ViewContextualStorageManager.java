/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
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
            return strategy.inCurrentContext(viewName, parameters);
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
