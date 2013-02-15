/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.cdi.deploy;

import com.vaadin.cdi.Root;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.VaadinCDIServlet;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

/**
 * TODO Document me!
 */
@WebListener
public class Deployer implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(Deployer.class.getCanonicalName());
    @Inject
    private BeanManager beanManager;
    private Set<String> configuredUIs;
    @Inject
    private VaadinCDIServlet vaadinCDIServlet;
    private String urlMapping = "/*";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configuredUIs = new HashSet<String>();

        ServletContext context = sce.getServletContext();

        logger.log(Level.INFO, "Initializing web context for path {0}", context.getContextPath());
        try {
            discoverUIMappingsFromAnnotations();
            discoverURLMappingFromRoot();
        } catch (InconsistentDeploymentException e) {
            vaadinCDIServlet.stopDeployment(e.toString());
            throw e;
        }

        deployVaadinCDIServlet(context);

        logger.info("Done deploying Vaadin UIs");
    }

    private boolean isVaadinServletDefinedInDeploymentDescriptor(
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
                throw new InconsistentDeploymentException(InconsistentDeploymentException.ID.CLASS_NOT_FOUND, e);
            }
        }

        return false;
    }

    /**
     * Checks that there are no multiple roots assigned to same application with
     * same value
     */
    private void discoverUIMappingsFromAnnotations() {
        logger.info("Discovering Vaadin UIs...");

        Set<Bean<?>> uiBeans = beanManager.getBeans(UI.class,
                new AnnotationLiteral<Any>() {
                });

        logger.log(Level.INFO, "{0} beans inheriting from UI discovered!", uiBeans.size());

        for (Bean<?> uiBean : dropBeansWithOutVaadinUIAnnotation(uiBeans)) {
            Class<? extends UI> uiBeanClass = uiBean.getBeanClass().asSubclass(
                    UI.class);

            String uiMapping = Conventions.deriveMappingForUI(uiBeanClass);

            if (configuredUIs.contains(uiMapping)) {
                throw new InconsistentDeploymentException(InconsistentDeploymentException.ID.PATH_COLLISION,
                        "Multiple UIs configured with @VaadinUI(" + uiMapping + ")");
            }

            configuredUIs.add(uiMapping);
        }

        int numberOfRootUIs = getNumberOfRootUIs();

        if (numberOfRootUIs == 1) {
            logger.log(Level.INFO, "Vaadin UI {0} is marked as @Root, "
                    + "this UI is accessible from context root of deployment", getRootClassName());
        }
        if (numberOfRootUIs > 1) {
            throw new InconsistentDeploymentException(InconsistentDeploymentException.ID.MULTIPLE_ROOTS,
                    "Found multiple UIs configured with @Root annotation, "
                    + "only one UI can be root");
        }

        logger.log(Level.INFO, "Available Vaadin UIs for CDI deployment {0}", configuredUIs);
    }

    /**
     * @return number of UI beans annotated with
     * @Root annotation
     */
    private int getNumberOfRootUIs() {
        Set<Bean<?>> beans = getRootAnnotatedBeans();

        return beans.size();
    }

    /**
     * @return name of the root class
     */
    private String getRootClassName() {
        Set<Bean<?>> beans = getRootAnnotatedBeans();

        return beans.iterator().next().getBeanClass().getCanonicalName();
    }

    /**
     * Checks if there is a
     *
     * @Root with a urlMapping. If so, it retrieves the URL mapping from it.
     */
    private void discoverURLMappingFromRoot() {
        Set<Bean<?>> beans = getRootAnnotatedBeans();
        if (beans != null && !beans.isEmpty()) {
            Class<?> rootClass = beans.iterator().next().getBeanClass();
            Root rootAnnotation = rootClass
                    .getAnnotation(Root.class);
            urlMapping = rootAnnotation.urlMapping();
            logger.log(Level.INFO, "Will map VaadinCDIServlet to ''{0}''", urlMapping);
        }
    }

    /**
     * @return all UI beans annotated with
     * @Root annotation.
     */
    private Set<Bean<?>> getRootAnnotatedBeans() {
        Set<Bean<?>> beans = beanManager.getBeans(UI.class,
                new AnnotationLiteral<Root>() {
                    // TODO Implement this method to get the first test running!
                    String urlMapping() {
                        return "/*";
                    }
                });
        return beans;
    }

    /**
     * From the given set of beans, removes all without
     *
     * @VaadinUI annotation
     *
     * @param uiBeans
     * @return set of beans having
     * @VaadinUI annotation
     */
    Set<Bean<?>> dropBeansWithOutVaadinUIAnnotation(Set<Bean<?>> uiBeans) {
        Set<Bean<?>> result = new HashSet<Bean<?>>();

        for (Bean<?> bean : uiBeans) {
            Class<?> beanClass = bean.getBeanClass();
            if (beanClass.isAnnotationPresent(VaadinUI.class)) {
                result.add(bean);
            } else {
                logger.log(Level.WARNING, "UI without VaadinUI annotation found: {0}, "
                        + "it is not available in CDI deployment", beanClass.getName());
            }
        }

        return result;
    }

    /**
     * Deploys VaadinCDIServlet to context root if UI classes with proper
     * annotation are available.
     */
    private void deployVaadinCDIServlet(ServletContext context) {
        if (isVaadinServletDefinedInDeploymentDescriptor(context)) {
            logger.warning(
                    "Vaadin related servlet is defined in deployment descriptor, "
                    + "automated deployment of VaadinCDIServlet is now disabled");
            return;
        }

        if (configuredUIs.isEmpty()) {
            logger.warning(
                    "No Vaadin UI classes with @VaadinUI or @Root annotation found. "
                    + "Skipping automated deployment of VaadinCDIServlet.");
            return;
        }

        registerServletToContext(context);

    }

    private void registerServletToContext(ServletContext context) {
        logger.info("Registering VaadinCDIServlet");
        final ServletRegistration.Dynamic registration = context.addServlet(
                "VaadinCDIServlet", vaadinCDIServlet);

        logger.log(Level.INFO, "Mapping {0} to {1}", new Object[]{registration.getName(), urlMapping});
        registration.addMapping("/VAADIN/*");
        registration.addMapping(urlMapping);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Context destroyed");
    }
}
