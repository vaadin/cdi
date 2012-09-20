package com.vaadin.cdi;

import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.vaadin.server.*;

public class VaadinCDIServlet extends VaadinServlet {

    @Inject
    private Instance<CDIUIProvider> cdiRootProvider;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        logger().info("VaadinCDIServlet initialized");
        this.registerUIProvider();
    }

    private void registerUIProvider() {
        getVaadinService().addVaadinSessionInitializationListener(
                new VaadinSessionInitializationListener() {
                    @Override
                    public void vaadinSessionInitialized(
                            VaadinSessionInitializeEvent vaadinSessionInitializeEvent)
                            throws ServiceException {
                        VaadinSession vaadinSession = vaadinSessionInitializeEvent
                                .getVaadinSession();
                        logger().info("sessionInitialized");
                        CDIUIProvider uiProvider = cdiRootProvider.get();
                        logger().info(
                                "Registering ui CDIUIProvider: " + uiProvider);
                        vaadinSession.addUIProvider(uiProvider);
                    }
                });
    }

    private static Logger logger() {
        return Logger.getLogger(VaadinCDIServlet.class.getCanonicalName());
    }
}
