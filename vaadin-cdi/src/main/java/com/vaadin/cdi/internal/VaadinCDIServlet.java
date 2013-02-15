/*
 * Copyright 2012 Vaadin Ltd.
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
package com.vaadin.cdi.internal;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Vaadin servlet that retrieves UIs using the {@link CDIUIProvider}. This class
 * is not part of the public API and should not be used by clients directly.
 */
public class VaadinCDIServlet extends VaadinServlet {

    private static final Logger logger = Logger.getLogger(VaadinCDIServlet.class.getCanonicalName());
    @Inject
    private CDIUIProvider cdiUIProvider;
    private static String deploymentAbortionReason = null;
    private final SessionInitListener sessionInitListener = new SessionInitListener() {
        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
            final VaadinSession session = event.getSession();
            logger.log(Level.INFO, "Registering CDIUIProvider: {0}", cdiUIProvider);
            session.addUIProvider(cdiUIProvider);
        }
    };

    /**
     * Call to this method prevents successful deployment of the
     * VaadinCDIServlet. This method is a workaround, because ContextDeployer is
     * not able to stop the deployment reliably (tested with TomEE 1.5)
     */
    public void stopDeployment(String reason) {
        VaadinCDIServlet.deploymentAbortionReason = reason;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        if (deploymentAbortionReason != null) {
            String message = deploymentAbortionReason;
            deploymentAbortionReason = null;
            throw new ServletException("VaadinCDIServlet deployment aborted. Reason: " + message);
        }
        logger.info("VaadinCDIServlet initialized");
        getService().addSessionInitListener(sessionInitListener);
    }
}
