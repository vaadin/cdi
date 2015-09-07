/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.cdi.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.URLMapping;
import com.vaadin.cdi.internal.InconsistentDeploymentException.ID;
import com.vaadin.cdi.server.VaadinCDIServlet;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.Version;
import com.vaadin.ui.UI;

@WebListener
public class ContextDeployer implements ServletContextListener {

    @Inject
    private BeanManager beanManager;

    private Set<String> configuredUIs;

    private String urlMapping = "/*";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        verifyCompatibleVaadinVersion();

        configuredUIs = new HashSet<String>();

        ServletContext context = sce.getServletContext();

        getLogger()
                .info("Initializing web context for path "
                        + context.getContextPath());
        try {
            discoverUIMappingsFromAnnotations();
            discoverURLMappingFromRoot();
        } catch (InconsistentDeploymentException e) {
            getLogger().severe(
                    "Vaadin CDI servlet deployment failed: " + e.toString());
            throw e;
        }

        deployVaadinServlet(context);

        getLogger().info("Done deploying Vaadin UIs");
    }

    private Class getVaadinServletDefinedInDeploymentDescriptor(
            ServletContext context) {
        for (ServletRegistration servletRegistration : context
                .getServletRegistrations().values()) {
            String servletClassName = servletRegistration.getClassName();

            // For preliminary registrations / JSP servlets, class name is null.
            // These should not prevent registration of the VaadinCDIServlet.
            if (null != servletClassName) {
                try {
                    if (servletClassName.equals("com.ibm.ws.wsoc.WsocServlet")) {
                        // Websphere servlet which implements websocket
                        // endpoints, dynamically added
                        continue;
                    }

                    Class<?> servletClass = context.getClassLoader().loadClass(
                            servletClassName);

                    if (VaadinServlet.class.isAssignableFrom(servletClass)) {
                        // return the first Vaadin servlet in the deployment
                        // descriptor
                        return servletClass;
                    }

                } catch (ClassNotFoundException e) {
                    throw new InconsistentDeploymentException(
                            InconsistentDeploymentException.ID.CLASS_NOT_FOUND, e);
                }
            }
        }

        return null;
    }

    /**
     * Checks that there are no multiple roots assigned to same application with
     * same value
     */
    private void discoverUIMappingsFromAnnotations() {
        Set<String> informativeMapping = new HashSet<String>();
        getLogger().info("Discovering Vaadin UIs...");

        Set<Bean<?>> uiBeans = AnnotationUtil.getUiBeans(beanManager);

        getLogger().info(
                uiBeans.size() + " beans inheriting from UI discovered!");

        for (Bean<?> uiBean : dropBeansWithOutVaadinUIAnnotation(uiBeans)) {
            Class<? extends UI> uiBeanClass = uiBean.getBeanClass().asSubclass(
                    UI.class);

            String uiMapping = Conventions.deriveMappingForUI(uiBeanClass);

            if (configuredUIs.contains(uiMapping)) {
                if ("".equals(uiMapping)) {
                    throw new InconsistentDeploymentException(
                            InconsistentDeploymentException.ID.MULTIPLE_ROOTS,
                            "Multiple UIs configured with @CDIUI annotation without context path, "
                                    + "only one UI can be root");
                } else {
                    throw new InconsistentDeploymentException(
                            InconsistentDeploymentException.ID.PATH_COLLISION,
                            "Multiple UIs configured with @CDIUI(" + uiMapping
                                    + ")");
                }
            }

            configuredUIs.add(uiMapping);
            informativeMapping.add("/" + uiMapping + " => "
                    + uiBean.getBeanClass().getSimpleName());
        }

        int numberOfRootUIs = getNumberOfRootUIs();

        if (numberOfRootUIs == 1) {
            getLogger()
                    .info("Vaadin UI "
                            + getRootClassName()
                            + " is marked as @CDIUI without context path, "
                            + "this UI is accessible from context root of deployment");
        }

        getLogger()
                .info("Available Vaadin UIs for CDI deployment "
                        + informativeMapping);
    }

    /**
     * @return number of UI beans annotated with {@link CDIUI} annotation with
     *         an empty context path.
     */
    private int getNumberOfRootUIs() {
        Set<Bean<?>> beans = AnnotationUtil.getRootUiBeans(beanManager);

        return beans.size();
    }

    /**
     * @return name of the root class
     */
    private String getRootClassName() {
        Set<Bean<?>> beans = AnnotationUtil.getRootUiBeans(beanManager);

        return beans.iterator().next().getBeanClass().getCanonicalName();
    }

    /**
     * Checks if there is a bean with no path for {@link CDIUI} but with a
     * urlMapping. If so, it retrieves the URL mapping from it.
     */
    private void discoverURLMappingFromRoot() {
        Set<Bean<?>> beans = AnnotationUtil.getRootUiBeans(beanManager);
        if (beans != null && !beans.isEmpty()) {
            Class<?> rootClass = beans.iterator().next().getBeanClass();

            URLMapping urlMappingAnnotation = rootClass
                    .getAnnotation(URLMapping.class);
            if (urlMappingAnnotation != null) {
                urlMapping = urlMappingAnnotation.value();
                getLogger().info(
                        "Will map VaadinCDIServlet to '" + urlMapping + "'");
            }
        }
    }

    /**
     * From the given set of beans, removes all without @CDIUI annotation
     * 
     * @param uiBeans
     * @return set of beans having @CDIUI annotation
     */
    Set<Bean<?>> dropBeansWithOutVaadinUIAnnotation(Set<Bean<?>> uiBeans) {
        Set<Bean<?>> result = new HashSet<Bean<?>>();

        for (Bean<?> bean : uiBeans) {
            Class<?> beanClass = bean.getBeanClass();

            if (beanClass.isAnnotationPresent(CDIUI.class)) {
                result.add(bean);
            } else {
                getLogger().warning(
                        "UI without CDIUI annotation found: "
                                + beanClass.getName()
                                + ", it is not available in CDI deployment");
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
    private void deployVaadinServlet(ServletContext context) {
        Class vaadinServletClass = getVaadinServletDefinedInDeploymentDescriptor(context);
        if (vaadinServletClass != null) {
            getLogger()
                    .warning(
                            "Vaadin related servlet is defined in deployment descriptor, "
                                    + "automated deployment of VaadinCDIServlet is now disabled");
            Class enclosingClass = vaadinServletClass.getEnclosingClass();
            if (!VaadinCDIServlet.class.isAssignableFrom(vaadinServletClass)
                    && enclosingClass != null
                    && enclosingClass.isAnnotationPresent(CDIUI.class)) {
                throw new InconsistentDeploymentException(
                        ID.EMBEDDED_SERVLET,
                        "A Vaadin @CDIUI class should not contain a nested servlet class ("
                                + vaadinServletClass.getCanonicalName()
                                + ") other than a VaadinCDIServlet. In most cases, an appropriate servlet is auto-deployed.");
            }
            return;
        }

        if (configuredUIs.isEmpty()) {
            getLogger()
                    .warning(
                            "No Vaadin UI classes with @CDIUI annotation found. "
                                    + "Skipping automated deployment of VaadinCDIServlet.");
            return;
        }

        registerServletToContext(context);

    }

    private void registerServletToContext(ServletContext context) {
        getLogger().info("Registering VaadinServlet with CDIUIProvider");

        VaadinServlet vaadinServlet = new VaadinCDIServlet();

        ServletRegistration.Dynamic registration = context.addServlet(
                "VaadinServlet", vaadinServlet);

        registration.setAsyncSupported(true);

        registration.addMapping("/VAADIN/*");
        getLogger().info(
                "Mapping " + registration.getName() + " to " + urlMapping);
        registration.addMapping(urlMapping);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        getLogger().info("Vaadin web context destroyed");
    }

    private static Logger getLogger() {
        return Logger.getLogger(ContextDeployer.class.getCanonicalName());
    }

    private void verifyCompatibleVaadinVersion() {
        boolean compatible = false;
        if (Version.getMajorVersion() > 7) {
            compatible = true;
        } else if (Version.getMajorVersion() == 7) {
            if (Version.getMinorVersion() > 3) {
                compatible = true;
            } else if (Version.getMinorVersion() == 3
                    && Version.getRevision() >= 1) {
                compatible = true;
            }
        }

        if (!compatible) {
            getLogger()
                    .warning(
                            "This Vaadin version is incompatible with the Vaadin CDI plugin. Vaadin CDI requires at least version 7.3.1, your version is "
                                    + Version.getFullVersion());
        }
    }
}
