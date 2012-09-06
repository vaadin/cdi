package com.vaadin.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.Registration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

@WebListener
public class ContextDeployer implements ServletContextListener {

    private Map<ServletRegistration, Set<String>> servletMappings;
    private Set<String> configuredUIs;

    @Inject
    private BeanManager beanManager;

    @Inject
    private Instance<VaadinCDIApplicationServlet> servletInstanceProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configuredUIs = new HashSet<String>();
        servletMappings = new HashMap<ServletRegistration, Set<String>>();

        ServletContext context = sce.getServletContext();

        getLogger()
                .info("Initializing web context for path "
                        + context.getContextPath());

        discoverDeployedServlets(context);
        discoverUIMappingsFromConfigurationFile(context);
        discoverUIMappingsFromAnnotations();

        deployVaadinCDIServletIfUIsAvailable(context);

        getLogger().info("Done deploying Vaadin UIs");
    }

    private void discoverDeployedServlets(ServletContext context) {
        Map<String, ? extends Registration> registrations = context
                .getServletRegistrations();

        getLogger().info("Discovering deployed servlets...");

        for (Registration registration : registrations.values()) {
            if (registration instanceof ServletRegistration) {
                ServletRegistration servletRegistration = (ServletRegistration) registration;

                String servletClassname = servletRegistration.getClassName();

                servletMappings.put(servletRegistration, new HashSet<String>());

                for (String mapping : servletRegistration.getMappings()) {
                    getLogger().info(
                            "Found " + servletClassname + " mapped to "
                                    + mapping);
                    servletMappings.get(servletRegistration).add(mapping);
                }
            }
        }
    }

    private boolean isServletMappedToContextRoot() {
        for (ServletRegistration servletRegistration : servletMappings.keySet()) {
            if (servletRegistration.getMappings().contains("/*")) {
                return true;
            }
        }

        return false;
    }

    private boolean isVaadinCDIServletMappedToContextRootInDeploymentDescriptor(
            ServletContext context) {
        for (ServletRegistration servletRegistration : servletMappings.keySet()) {
            String servletClassName = servletRegistration.getClassName();

            try {
                Class<?> servletClass = context.getClassLoader().loadClass(
                        servletClassName);

                if (VaadinCDIApplicationServlet.class
                        .isAssignableFrom(servletClass)) {

                    if (servletRegistration.getMappings().contains("/*")) {
                        return true;
                    }
                }

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return false;
    }

    private void discoverUIMappingsFromConfigurationFile(ServletContext context) {

        getLogger().info(
                "Discovering Vaadin UI mappings from deployment descriptor...");

        for (ServletRegistration servletRegistration : servletMappings.keySet()) {
            try {
                String uiClassName = servletRegistration
                        .getInitParameter(VaadinSession.UI_PARAMETER);

                if (uiClassName != null) {
                    Class<?> uiClass = context.getClassLoader().loadClass(
                            uiClassName);

                    if (uiClass.isAnnotationPresent(VaadinUI.class)) {
                        throw new RuntimeException(
                                uiClass
                                        + " contains both, web.xml mapping as well as annotation configuration, only either one is allowed");
                    }
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not find UI class", e);
            }
        }
    }

    /**
     * Checks that there are no multiple roots assigned to same application with
     * same mapping
     */
    private void discoverUIMappingsFromAnnotations() {
        getLogger().info(
                "Discovering Vaadin UI mappings from @VaadinUI annotations...");

        Set<Bean<?>> uiBeans = beanManager.getBeans(UI.class,
                new VaadinUIAnnotation());

        for (Bean<?> uiBean : uiBeans) {
            Class<? extends UI> uiBeanClass = uiBean.getBeanClass().asSubclass(
                    UI.class);

            if (uiBeanClass.isAnnotationPresent(VaadinUI.class)) {
                VaadinUI vaadinUIAnnotation = uiBeanClass
                        .getAnnotation(VaadinUI.class);

                String uiMapping = vaadinUIAnnotation.mapping();

                if (configuredUIs.contains(uiMapping)) {
                    throw new RuntimeException(
                            "Multiple UIs configured with same mapping "
                                    + uiMapping);
                }

                configuredUIs.add(uiMapping);
            }
        }

        getLogger().info(
                "Available Vaadin UIs for CDI deployment " + configuredUIs);
    }

    /**
     * Deploys VaadinCDIServlet to context root if UI classes with proper
     * annotation are available
     * 
     * @param context
     */
    private void deployVaadinCDIServletIfUIsAvailable(ServletContext context) {
        if (configuredUIs.isEmpty()) {
            getLogger()
                    .warning(
                            "No Vaadin UI classes with @VaadinUI annotation found. Skipping automated deployment of VaadinCDIServlet.");
        } else {
            if (!isServletMappedToContextRoot()) {
                getLogger()
                        .info("No other deployment descriptor defined servlets found from context root");
                registerVaadinCDIServletToContextRoot(context);
            } else {
                if (isVaadinCDIServletMappedToContextRootInDeploymentDescriptor(context)) {
                    getLogger()
                            .info("Servlet capable of deploying Vaadin UIs with CDI is already defined in deployment descriptor to context root, this will be used instead of default deployment of "
                                    + VaadinCDIApplicationServlet.class
                                            .getName());
                } else {
                    getLogger()
                            .warning(
                                    "Could not register VaadinCDIServlet automatically to context root as there is another servlet defined in deployment descriptor, @VaadinUI annotated UIs are not available");
                }
            }
        }
    }

    private void registerVaadinCDIServletToContextRoot(ServletContext context) {
        getLogger().info(
                "Attempt to deploy VaadinCDIServlet to context root...");
        registerServletToContext("/*", context);
    }

    private void registerServletToContext(String mapping, ServletContext context) {
        getLogger().info("Registering VaadinCDIServlet");

        ServletRegistration.Dynamic registration = context.addServlet(
                "VaadinCDIServlet", servletInstanceProvider.get());

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
        getLogger()
                .info("Mapping " + registration.getName() + " to " + mapping);
        registration.addMapping(mapping);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Context destroyed");
    }

    private static Logger getLogger() {
        return Logger.getLogger(ContextDeployer.class.getCanonicalName());
    }
}
