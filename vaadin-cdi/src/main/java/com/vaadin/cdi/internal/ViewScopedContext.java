/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.ui.UI;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

/**
 * ViewScopedContext is the context for @ViewScoped beans.
 */
public class ViewScopedContext extends AbstractContext {

    private ViewContextualStorageManager contextualStorageManager;

    public ViewScopedContext(BeanManager beanManager) {
        super(beanManager);
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        return contextualStorageManager.getContextualStorage(createIfNotExist);
    }

    public void init(BeanManager beanManager) {
        contextualStorageManager = BeanProvider
                .getContextualReference(beanManager, ViewContextualStorageManager.class, false);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ViewScoped.class;
    }

    @Override
    public boolean isActive() {
        return UI.getCurrent() != null
                && contextualStorageManager != null
                && contextualStorageManager.isActive();
    }
}
