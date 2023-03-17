/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.instantiatorcustomize;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.NavigationEvent;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

@Priority(Interceptor.Priority.APPLICATION)
@Decorator
public abstract class InstantiatorDecorator implements Instantiator {
    @Inject
    @Delegate
    @VaadinServiceEnabled
    private Instantiator delegate;

    @Override
    public <T> T getOrCreate(Class<T> type) {
        T instance = delegate.getOrCreate(type);
        if (InstantiatorCustomizeView.class.equals(type)) {
            ((InstantiatorCustomizeView) instance).customize();
        }
        return instance;
    }

    @Override
    public <T extends HasElement> T createRouteTarget(Class<T> routeTargetType, NavigationEvent event) {
        // Need to override it too to make it work on both Weld and OWB.
        return getOrCreate(routeTargetType);
    }
}
