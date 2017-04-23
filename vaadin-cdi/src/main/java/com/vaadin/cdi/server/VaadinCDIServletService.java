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

import com.vaadin.cdi.CDIUIProvider;
import com.vaadin.cdi.internal.VaadinSessionScopedContext;
import com.vaadin.server.*;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import java.util.logging.Logger;

/**
 * Servlet service implementation for Vaadin CDI.
 * 
 * This class automatically initializes CDIUIProvider and provides the CDI
 * add-on events about session and request processing. For overriding this
 * class, see VaadinCDIServlet.
 */
public class VaadinCDIServletService extends VaadinServletService {

    private final CDIUIProvider cdiuiProvider;

    protected final class SessionListenerImpl implements SessionInitListener,
            SessionDestroyListener {
        @Override
        public void sessionInit(SessionInitEvent event) {
            getLogger().fine("Session init");
            event.getSession().addUIProvider(cdiuiProvider);
        }

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            getLogger().fine("VaadinSessionScopedContext destroy");
            if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
                // Happens on tomcat when it expires sessions upon undeploy.
                // beanManager.getPassivationCapableBean returns null for passivation id,
                // so we would get an NPE from AbstractContext.destroyAllActive
                getLogger().warning("VaadinSessionScoped context does not exist. " +
                                "Maybe application is undeployed." +
                                " Can't destroy VaadinSessionScopedContext.");
                return;
            }
            VaadinSessionScopedContext.destroy(event.getSession());
        }

    }

    public VaadinCDIServletService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(servlet, deploymentConfiguration);

        cdiuiProvider = BeanProvider.getContextualReference(CDIUIProvider.class, false);
        SessionListenerImpl sessionListener = new SessionListenerImpl();
        addSessionInitListener(sessionListener);
        addSessionDestroyListener(sessionListener);
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinCDIServletService.class
                .getCanonicalName());
    }

}
