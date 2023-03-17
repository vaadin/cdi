/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import org.apache.deltaspike.core.util.context.AbstractContext;

import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import java.lang.annotation.Annotation;

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
