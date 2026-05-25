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

import org.apache.deltaspike.core.util.context.AbstractContext;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.spi.AlterableContext;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;

/**
 * Used to bind multiple scope annotations to a single context. Will delegate
 * all context-related operations to it's underlying instance, apart from
 * getting the scope of the context.
 *
 */
public class ContextWrapper implements AlterableContext {

    private final AbstractContext context;
    private final Class<? extends Annotation> scope;

    public ContextWrapper(AbstractContext context, Class<? extends Annotation> scope) {
        this.context = context;
        this.scope = scope;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public <T> T get(Contextual<T> component,
            CreationalContext<T> creationalContext) {
        return context.get(component, creationalContext);
    }

    @Override
    public <T> T get(Contextual<T> component) {
        return context.get(component);
    }

    @Override
    public boolean isActive() {
        return context.isActive();
    }

    @Override
    public void destroy(Contextual<?> contextual) {
        context.destroy(contextual);
    }
}
