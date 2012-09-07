package com.vaadin.cdi;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletSession;

public class VaadinCDIServlet extends VaadinServlet {

    @Inject
    private Instance<CDIUIProvider> cdiRootProvider;

    @Override
    protected VaadinServletSession createApplication(HttpServletRequest request)
            throws ServletException {

        VaadinServletSession newApplication = new VaadinServletSession();

        newApplication.addUIProvider(cdiRootProvider.get());

        return newApplication;
    }
}