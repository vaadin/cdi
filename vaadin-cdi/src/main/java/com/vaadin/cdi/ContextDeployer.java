package com.vaadin.cdi;

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

    private Set<String> configuredUIs;

    @Inject
    @Any
    private Instance<UI> uis;

    @Inject
    private Instance<VaadinCDIApplicationServlet> servletInstanceProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configuredUIs = new HashSet<String>();

        ServletContext context = sce.getServletContext();

        System.out.println("Initializing web context for path "
                + context.getContextPath());

        discoverUIMappingsFromConfigurationFile(context);
        discoverUIMappingsFromAnnotations();

        registerVaadinApplications(context);
    }

    private void discoverUIMappingsFromConfigurationFile(ServletContext context) {
        Map<String, ? extends Registration> registrations = context
                .getServletRegistrations();

        for (Registration registration : registrations.values()) {
            String uiParameter = registration
                    .getInitParameter(Application.UI_PARAMETER);

            if (uiParameter != null) {
                System.out.println(uiParameter
                        + " is already configured in web.xml");
                configuredUIs.add(uiParameter);
            }
        }
    }

    /**
     * Checks that there are no multiple roots assigned to same application with
     * same mapping
     */
    private void discoverUIMappingsFromAnnotations() {
        for (UI ui : uis) {

            if (ui.getClass().isAnnotationPresent(VaadinUI.class)) {
                VaadinUI vaadinUIAnnotation = ui.getClass().getAnnotation(
                        VaadinUI.class);

                String rootMapping = vaadinUIAnnotation.mapping();

                if (configuredUIs.contains(rootMapping)) {
                    throw new RuntimeException(
                            "Multiple UIs configured with same mapping "
                                    + rootMapping);
                }

                configuredUIs.add(rootMapping);
            }
        }

        System.out.println("Available UIs: " + configuredUIs);
    }

    /**
     * Registers all discovered applications to given servlet context
     * 
     * @param context
     */
    private void registerVaadinApplications(ServletContext context) {
        if (configuredUIs.isEmpty()) {
            System.out
                    .println("Could not register Vaadin UIs, no classes found with @VaadinUI annotations and or no UI configured to web.xml with UI parameter.");
        }

        registerDefaultApplicationToContext(context);
    }

    private void registerDefaultApplicationToContext(ServletContext context) {
        registerApplicationToContext(Application.class, "/*", context);
    }

    private void registerApplicationToContext(
            Class<? extends Application> applicationClass, String mapping,
            ServletContext context) {
        String className = applicationClass.getSimpleName();
        String canonicalClassName = applicationClass.getCanonicalName();

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
}
