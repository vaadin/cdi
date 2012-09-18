package com.vaadin.cdi;

import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;

import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletSession;
import com.vaadin.server.WrappedHttpServletRequest;

public class VaadinCDIServlet extends VaadinServlet {

    @Inject
    private Instance<CDIUIProvider> cdiRootProvider;

    @Override
    protected void onVaadinSessionStarted(WrappedHttpServletRequest request,
            VaadinServletSession session) throws ServletException {
        logger().info("onVaadinSessionStarted");
        CDIUIProvider uiProvider = cdiRootProvider.get();
        logger().info("Registering ui CDIUIProvider: " + uiProvider);
        session.addUIProvider(uiProvider);
        super.onVaadinSessionStarted(request, session);
    }

    private static Logger logger() {
        return Logger.getLogger(VaadinCDIServlet.class.getCanonicalName());
    }
}