package com.vaadin.terminal.gwt.server;

import com.vaadin.Application;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class VaadinCDIApplicationServlet extends AbstractApplicationServlet {

    private Class<? extends Application> vaadinApplicationClass;
    @Inject @Any
    private Instance<Application> applications;

    @Override
    public void init() throws ServletException {
        super.init();
        String applicationClassName = getInitParameter("application");
        System.out.println("Initializing servlet for application "+ applicationClassName);
        try {
            //why you are doing this? @VaadinApplication is supposed to be unique
            vaadinApplicationClass = (Class<? extends Application>) getServletContext()
                    .getClassLoader().loadClass(applicationClassName);
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find application class for "
                    + applicationClassName);
            throw new ServletException(e);
        }
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {
        Instance<Application> filtered = (Instance<Application>) applications.select(vaadinApplicationClass, new VaadinApplicationInstance());
        for (Application application : filtered) {
                return application;
       }
        throw new ServletException("No Vaadin application bean found for class: " + vaadinApplicationClass.getName());
    }

    @Override
    public Class<? extends Application> getApplicationClass() {
        return vaadinApplicationClass;
    }
}