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
package com.vaadin.cdi.server;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIUIProvider;
import com.vaadin.cdi.internal.ContextDeployer;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 * Servlet for {@link CDIUI}s. Through VaadinCDIServletService, this servlet
 * automatically uses {@link CDIUIProvider}.
 * 
 * An instance of this servlet is automatically deployed by
 * {@link ContextDeployer} if no VaadinServlet is deployed based on web.xml or
 * Servlet 3.0 annotations. A subclass of this servlet and of
 * VaadinCDIServletService can be used and explicitly deployed to customize e.g.
 * system messages, in which case
 * {@link #createServletService(DeploymentConfiguration)} must call
 * service.init() .
 */
public class VaadinCDIServlet extends VaadinServlet {

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        VaadinCDIServletService service = new VaadinCDIServletService(this,
                deploymentConfiguration);
        service.init();
        return service;
    }

}
