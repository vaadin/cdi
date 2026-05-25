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

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public class UIScopedContext extends AbstractContext {

    private UIContextualStorageManager contextualStorageManager;

    public UIScopedContext(final BeanManager beanManager) {
        super(beanManager);
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        return contextualStorageManager.getContextualStorage(createIfNotExist);
    }

    public void init(BeanManager beanManager) {
        contextualStorageManager = BeanProvider
                .getContextualReference(beanManager, UIContextualStorageManager.class, false);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    public boolean isActive() {
        return VaadinSession.getCurrent() != null
                && contextualStorageManager != null
                && contextualStorageManager.isActive();
    }

}
