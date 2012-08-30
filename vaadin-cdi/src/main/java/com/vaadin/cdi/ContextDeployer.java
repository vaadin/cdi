package com.vaadin.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.Registration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import com.vaadin.Application;
import com.vaadin.ui.UI;

@WebListener
public class ContextDeployer implements ServletContextListener {

    private Set<String> configuredApplications;
    private Map<String, Set<String>> uiMappings;

    @Inject
    @VaadinApplication
    private Instance<Application> applications;

    @Inject
    @Any
    private Instance<UI> uis;

    @Inject
    private Instance<VaadinCDIApplicationServlet> servletInstanceProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configuredApplications = new HashSet<String>();
        uiMappings = new HashMap<String, Set<String>>();

        ServletContext context = sce.getServletContext();

        System.out.println("Initializing web context for path "
                + context.getContextPath());

        discoverMappingDoneInConfigurationFile(context);
        discoverApplicationMappingsAndThrowOnConflicts();
        discoverUIMappingsAndThrowOnConflicts();

        registerVaadinApplications(context);
    }

    private void discoverMappingDoneInConfigurationFile(ServletContext context) {
        Map<String, ? extends Registration> registrations = context
                .getServletRegistrations();

        for (Registration registration : registrations.values()) {
            String applicationParameter = registration
                    .getInitParameter("application");

            if (applicationParameter != null) {
                System.out.println(applicationParameter
                        + " is already configured in web.xml");
                configuredApplications.add(applicationParameter);
            }
        }
    }

    /**
     * Checks whether there are multiple Vaadin application in class path
     * annotated with @VaadinApplication annotation using same mapping.
     */
    private void discoverApplicationMappingsAndThrowOnConflicts() {
        for (Application application : applications) {
            VaadinApplication applicationAnnotation = application.getClass()
                    .getAnnotation(VaadinApplication.class);

            String mapping = applicationAnnotation.mapping();

            if (!mapping.startsWith("/")) {
                throw new RuntimeException("Mapping for application "
                        + application.getClass().getSimpleName()
                        + " does not start with /");
            }

            if (uiMappings.containsKey(mapping)) {
                throw new RuntimeException(
                        "Multiple Vaadin applications annotated with @VaadinApplication have same mapping attribute value or no mapping specified.");
            }

            uiMappings.put(mapping, new HashSet<String>());
        }
    }

    /**
     * Checks that there are no multiple roots assigned to same application with
     * same mapping
     */
    private void discoverUIMappingsAndThrowOnConflicts() {
        for (UI ui : uis) {

            if (ui.getClass().isAnnotationPresent(VaadinUI.class)) {
                VaadinUI vaadinUIAnnotation = ui.getClass().getAnnotation(
                        VaadinUI.class);
                Class<? extends Application> applicationClass = vaadinUIAnnotation
                        .application();

                String applicationMapping = "/*";
                String rootMapping = vaadinUIAnnotation.mapping();

                if (applicationClass
                        .isAnnotationPresent(VaadinApplication.class)) {
                    VaadinApplication vaadinApplicationAnnotation = applicationClass
                            .getAnnotation(VaadinApplication.class);

                    applicationMapping = vaadinApplicationAnnotation.mapping();
                }

                if (!uiMappings.containsKey(applicationMapping)) {
                    uiMappings.put(applicationMapping, new HashSet<String>());
                }

                if (uiMappings.get(applicationMapping).contains(rootMapping)) {
                    throw new RuntimeException("Application "
                            + applicationMapping
                            + " has multiple roots with same mapping "
                            + rootMapping);
                }

                uiMappings.get(applicationMapping).add(rootMapping);
            }
        }

        for (String applicationMapping : uiMappings.keySet()) {
            System.out.println(applicationMapping + " "
                    + uiMappings.get(applicationMapping));
        }
    }

    /**
     * Registers all discovered applications to given servlet context
     * 
     * @param context
     */
    private void registerVaadinApplications(ServletContext context) {
        if (uiMappings.isEmpty()) {
            System.out
                    .println("Could not register Vaadin applications or UIs, no such classes found with @VaadinApplication or @VaadinUI annotations");
        }

        if (isApplicationsWithAnnotationsSpecified()) {
            for (Application vaadinApplication : applications) {
                registerApplicationToContext(vaadinApplication, context);
            }
        }

        if (isUIsToDefaultApplicationSpecified()) {
            if (!isApplicationRegisteredToContextUI(context)) {
                registerDefaultApplicationToContext(context);
            }
        }
    }

    private void registerDefaultApplicationToContext(ServletContext context) {
        registerApplicationToContext(Application.class, "/*", context);
    }

    private boolean isApplicationsWithAnnotationsSpecified() {
        return !applications.isUnsatisfied();
    }

    private boolean isUIsToDefaultApplicationSpecified() {
        if (uiMappings.containsKey("/*")) {
            return !uiMappings.get("/*").isEmpty();
        }

        return false;
    }

    private boolean isApplicationRegisteredToContextUI(ServletContext context) {
        for (ServletRegistration registration : context
                .getServletRegistrations().values()) {
            if (registration.getMappings().contains("/*")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Registers given application to given servletContext
     * 
     * @param vaadinApplication
     * @param context
     */
    private void registerApplicationToContext(Application vaadinApplication,
            ServletContext context) {
        String mapping = getMappingForApplication(vaadinApplication);

        registerApplicationToContext(vaadinApplication.getClass(), mapping,
                context);
    }

    private void registerApplicationToContext(
            Class<? extends Application> applicationClass, String mapping,
            ServletContext context) {
        String className = applicationClass.getSimpleName();
        String canonicalClassName = applicationClass.getCanonicalName();

        // If mapping is already done in web.xml, skip registration
        if (configuredApplications.contains(canonicalClassName)) {
            System.out.println(canonicalClassName
                    + " is already registed, skipping");
            return;
        }

        System.out.println("Instantiating new servlet for "
                + canonicalClassName);

        ServletRegistration.Dynamic registration = context.addServlet(
                className, servletInstanceProvider.get());

        registration.setInitParameter("application",
                applicationClass.getCanonicalName());

        registration.addMapping("/VAADIN/*");

        addMappingToRegistration(mapping, registration);
    }

    /**
     * Adds given mapping to given servlet registration
     * 
     * @param mapping
     * @param registration
     */
    private void addMappingToRegistration(String mapping, Dynamic registration) {
        System.out.println("Mapping " + registration.getName() + " to "
                + mapping);
        registration.addMapping(mapping);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Context destroyed");
    }

    /**
     * @param application
     * @return intended URL mapping for given application
     */
    private String getMappingForApplication(Application application) {
        Class<? extends Application> applicationClass = application.getClass();

        if (applicationClass.isAnnotationPresent(VaadinApplication.class)) {
            VaadinApplication deploymentIdentifier = applicationClass
                    .getAnnotation(VaadinApplication.class);
            String mappingAttribute = deploymentIdentifier.mapping();

            if (mappingAttribute != null) {
                return mappingAttribute;
            }
        }
        return applicationClass.getSimpleName();
    }
}
