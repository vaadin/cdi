package com.vaadin.terminal.gwt.server;

import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import com.vaadin.Application;
import com.vaadin.cdi.VaadinApplication;

@WebListener
public class ContextDeployer implements ServletContextListener {

    @Inject
    @VaadinApplication
    private Instance<Application> applications;
    
    @Inject
    private Instance<VaadinCDIApplicationServlet> servletInstanceProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        System.out.println("Initializing web context for path "
                + context.getContextPath());
        registerVaadinApplications(context);
    }

    private void registerVaadinApplications(ServletContext context) {
        for (Application vaadinApplication : applications) {
            if (!isApplicationRegistered(vaadinApplication, context)) {
                registerApplicationToContext(vaadinApplication, context);
            } else {
                System.out.println(getApplicationName(vaadinApplication)
                        + " is already registered");
            }
        }
    }

    /**
     * @return true if only single default application is marked as deployable
     */
    private boolean isOnlySingleDefaultApplication() {
        return !applications.isUnsatisfied() && !applications.isAmbiguous();
    }

    private void registerApplicationToContext(Application vaadinApplication,
            ServletContext context) {
        String applicationName = getApplicationName(vaadinApplication);
        ServletRegistration.Dynamic registration = context.addServlet(
                applicationName, servletInstanceProvider.get());
        registration.setInitParameter("application", vaadinApplication
                .getClass().getCanonicalName());
        registration.addMapping("/" + applicationName + "/*");
        registration.addMapping("/VAADIN/*");
        if (isOnlySingleDefaultApplication()) {
            registration.addMapping("/*");
        }
        System.out.println("Registered Vaadin application marked as deployable "
                + applicationName + " to " + registration.getMappings());
    }

    private boolean isApplicationRegistered(
            Application vaadinApplication, ServletContext servletContext) {
        Map<String, ? extends ServletRegistration> registeredServlets = servletContext
                .getServletRegistrations();

        String applicationName = getApplicationName(vaadinApplication);

        if (registeredServlets.containsKey(applicationName)) {
            System.out
                    .println("Servlet mapping found for Vaadin application marked as deployable "
                    + applicationName
                    + " "
                    + registeredServlets.get(applicationName)
                    .getMappings());
            return true;
        }

        System.out
                .println("No servlet mapping found for Vaadin application marked as deployable "
                + applicationName);

        return false;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Context destroyed");
    }

    private String getApplicationName(Application application) {
        Class<? extends Application> applicationClass = application.getClass();

        if (applicationClass.isAnnotationPresent(VaadinApplication.class)) {
            VaadinApplication deploymentIdentifier = applicationClass
                    .getAnnotation(VaadinApplication.class);
            String mappingAttribute = deploymentIdentifier.mapping();
            if (mappingAttribute != null) {
                if (mappingAttribute.isEmpty()) {
                    return applicationClass.getSimpleName();
                } else {
                    return mappingAttribute;
                }
            }
        }
        return applicationClass.getSimpleName();
    }
}
