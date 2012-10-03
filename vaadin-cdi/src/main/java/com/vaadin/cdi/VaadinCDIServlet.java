package com.vaadin.cdi;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.vaadin.server.*;

public class VaadinCDIServlet extends VaadinServlet {

    @Inject
    private CDIUIProvider cdiRootProvider;

    private final SessionInitListener sessionInitListener = new SessionInitListener() {

        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
            VaadinService service = event.getService();
            final VaadinSession session = event.getSession();
            logger().info("Registering ui CDIUIProvider: " + cdiRootProvider);
            service.addUIProvider(session,cdiRootProvider);
        }
    };

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        logger().info("VaadinCDIServlet initialized");
        getService().addSessionInitListener(sessionInitListener);
    }

    private static Logger logger() {
        return Logger.getLogger(VaadinCDIServlet.class.getCanonicalName());
    }
}
