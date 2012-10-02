package com.vaadin.cdi;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.server.VaadinServlet;

public class VaadinCDIServlet extends VaadinServlet {

    @Inject
    private CDIUIProvider cdiRootProvider;

    private final SessionInitListener sessionInitListener = new SessionInitListener() {

        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
            VaadinServiceSession vaadinSession = event.getSession();
            logger().info("Registering ui CDIUIProvider: " + cdiRootProvider);
            vaadinSession.addUIProvider(cdiRootProvider);
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

    @Override
    public void service(ServletRequest servletRequest,
            ServletResponse servletResponse) throws ServletException,
            IOException {
        Request.set((HttpServletRequest) servletRequest);
        super.service(servletRequest, servletResponse);
        Request.cleanup();
    }
}
