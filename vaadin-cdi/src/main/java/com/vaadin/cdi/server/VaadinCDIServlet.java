/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
