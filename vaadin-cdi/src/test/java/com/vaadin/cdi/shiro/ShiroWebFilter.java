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
package com.vaadin.cdi.shiro;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.annotation.WebFilter;

import org.apache.shiro.web.servlet.ShiroFilter;

/**
 * Web filter to link Shiro authentication to the HTTP session. In a real
 * application, a custom session manager would typically be used - see
 * {@link ShiroTest}.
 * 
 * This subclass exists to register the filter with an annotation without using
 * web.xml .
 */
@WebFilter(filterName = "ShiroFilter", urlPatterns = { "/*" }, dispatcherTypes = {
        DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE,
        DispatcherType.ERROR })
public class ShiroWebFilter extends ShiroFilter {

}
