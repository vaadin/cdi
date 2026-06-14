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
package com.vaadin.cdi.uis;

import jakarta.interceptor.Interceptors;

/**
 */
@Interceptors(InstrumentedInterceptor.class)
public class InterceptedBean {

    public String fromInterceptorBean() {
        return "hello from intercepted bean";
    }
}
