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
package com.vaadin.cdi.uis;

import com.vaadin.cdi.internal.Counter;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

/**
 */
public class InstrumentedInterceptor {

    public static final String INTERCEPT_COUNT = "InstrumentedInterceptor";
    @Inject
    Counter counter;

    @AroundInvoke
    public Object intercept(InvocationContext invocationContext)
            throws Exception {
        System.out.println("---invoked: " + invocationContext.getMethod());
        counter.increment(INTERCEPT_COUNT);
        return invocationContext.proceed();

    }
}
