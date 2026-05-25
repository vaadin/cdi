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

import jakarta.servlet.annotation.WebListener;

import org.apache.shiro.web.env.EnvironmentLoaderListener;

/**
 * Initialize Shiro with shiro.ini on web application startup.
 * 
 * This subclass exists to register the listener with an annotation without
 * using web.xml .
 */
@WebListener
public class ShiroWebListener extends EnvironmentLoaderListener {

}
