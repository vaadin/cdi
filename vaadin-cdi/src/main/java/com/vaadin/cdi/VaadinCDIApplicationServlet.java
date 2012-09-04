package com.vaadin.cdi;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.server.VaadinServlet;

public class VaadinCDIApplicationServlet extends VaadinServlet {

    private final Class<Application> vaadinApplicationClass = Application.class;

    @Inject
    private Instance<CDIUIProvider> cdiRootProvider;

    @Override
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {

        Application application = null;

        try {
            application = vaadinApplicationClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to create application instance",
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create application instance",
                    e);
        }

        application.addUIProvider(cdiRootProvider.get());

        return application;
    }
}