package com.vaadin.cdi;

import java.io.IOException;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.*;
import com.vaadin.util.CurrentInstance;

public class VaadinCDIServlet extends VaadinServlet {

    @Inject
    private CDIUIProvider cdiRootProvider;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        logger().info("VaadinCDIServlet initialized");
        this.registerUIProvider();
    }

    private void registerUIProvider() {

        super.getService().addVaadinSessionInitializationListener(
                new VaadinSessionInitializationListener() {
                    @Override
                    public void vaadinSessionInitialized(
                            VaadinSessionInitializeEvent vaadinSessionInitializeEvent)
                            throws ServiceException {
                        VaadinSession vaadinSession = vaadinSessionInitializeEvent
                                .getSession();
                        VaadinService service = vaadinSessionInitializeEvent.getService();
                        logger().info("sessionInitialized");
                        logger().info(
                                "Registering ui CDIUIProvider: " + cdiRootProvider);
                        service.addUIProvider(vaadinSession, cdiRootProvider);
                    }
                });
    }

    private static Logger logger() {
        return Logger.getLogger(VaadinCDIServlet.class.getCanonicalName());
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Request.set((HttpServletRequest)servletRequest);
        super.service(servletRequest, servletResponse);
        Request.cleanup();
    }
}
