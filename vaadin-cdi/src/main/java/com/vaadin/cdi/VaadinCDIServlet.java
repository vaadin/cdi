package com.vaadin.cdi;

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
        session.addUIProvider(cdiRootProvider.get());
        super.onVaadinSessionStarted(request, session);
    }
}