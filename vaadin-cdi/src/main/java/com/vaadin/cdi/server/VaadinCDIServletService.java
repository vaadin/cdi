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
            if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
                // Happens on tomcat when it expires sessions upon undeploy.
                // beanManager.getPassivationCapableBean returns null for passivation id,
                // so we would get an NPE from AbstractContext.destroyAllActive
                getLogger().warning("VaadinSessionScoped context does not exist. " +
                                "Maybe application is undeployed." +
                                " Can't destroy VaadinSessionScopedContext.");
                return;
            }
            getLogger().fine("VaadinSessionScopedContext destroy");
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
