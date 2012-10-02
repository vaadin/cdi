package com.vaadin.cdi;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@WebListener
public class ContextDeployer implements ServletContextListener {

    @Inject
    private BeanManager beanManager;

    private Set<String> configuredUIs;
    
    private boolean foundRootUI = false;

    @Inject
    private Instance<VaadinCDIServlet> servletInstanceProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configuredUIs = new HashSet<String>();

        ServletContext context = sce.getServletContext();

        getLogger()
                .info("Initializing web context for path "
                        + context.getContextPath());

        discoverUIMappingsFromAnnotations();

        deployVaadinCDIServlet(context);

        getLogger().info("Done deploying Vaadin UIs");
    }

    private boolean isVaadinServletsDefinedInDeploymentDescriptor(
            ServletContext context) {
        for (ServletRegistration servletRegistration : context
                .getServletRegistrations().values()) {
            String servletClassName = servletRegistration.getClassName();

            try {
                Class<?> servletClass = context.getClassLoader().loadClass(
                        servletClassName);

                if (VaadinServlet.class.isAssignableFrom(servletClass)) {
                    return true;
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    /**
     * Checks that there are no multiple roots assigned to same application with
     * same value
     */
    private void discoverUIMappingsFromAnnotations() {
        getLogger().info(
                "Discovering Vaadin UI mappings from @Mapping annotations...");

        Set<Bean<?>> uiBeans = beanManager.getBeans(UI.class,
                new AnnotationLiteral<Any>() {
                });
        getLogger().info(
                uiBeans.size() + " Beans inheriting from UI discovered!");
        for (Bean<?> uiBean : dropNotAnnotated(uiBeans)) {
            Class<? extends UI> uiBeanClass = uiBean.getBeanClass().asSubclass(
                    UI.class);

            String uiMapping = Conventions.deriveMappingForUI(uiBeanClass);
            if (configuredUIs.contains(uiMapping)) {
                throw new RuntimeException(
                        "Multiple UIs configured with same value "
                                + uiMapping);
            }
            configuredUIs.add(uiMapping);
        }

        getLogger().info(
                "Available Vaadin UIs for CDI deployment " + configuredUIs);
    }

    Set<Bean<?>> dropNotAnnotated(Set<Bean<?>> uiBeans) {
        Set<Bean<?>> result = new HashSet<Bean<?>>();
        for(Bean<?> bean:uiBeans){
            Class<?> beanClass = bean.getBeanClass();
            if(beanClass.isAnnotationPresent(VaadinUI.class)) {
                if (!beanClass.isAnnotationPresent(Root.class)) {
                    result.add(bean);
                } else {
                    foundRootUI = true;
                    getLogger().info(
                            "Root UI found: " + beanClass.getName());
                }
            } else {
                getLogger().info(
                        "UI without VaadinUI annotation found: " + beanClass.getName());
            }
        }
        return result;
    }

    /**
     * Deploys VaadinCDIServlet to context root if UI classes with proper
     * annotation are available
     * 
     * @param context
     */
    private void deployVaadinCDIServlet(ServletContext context) {
        if (isVaadinServletsDefinedInDeploymentDescriptor(context)) {
            getLogger()
                    .warning(
                            "Vaadin related servlet is defined in deployment descriptor, automated deployment of VaadinCDIServlet is now disabled");
            return;
        }

        if (configuredUIs.isEmpty() && !foundRootUI) {
            getLogger()
                    .warning(
                            "No Vaadin UI classes with @Mapping annotation found. Skipping automated deployment of VaadinCDIServlet.");
            return;
        }

        registerVaadinCDIServletToContextRoot(context);

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
     * Adds given value to given servlet registration
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
