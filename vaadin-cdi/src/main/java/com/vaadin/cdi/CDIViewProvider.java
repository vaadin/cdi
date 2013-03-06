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

package com.vaadin.cdi;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.vaadin.cdi.access.JaasTools;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

public class CDIViewProvider implements ViewProvider {

    @Inject
    private BeanManager beanManager;
    private transient CreationalContext<?> currentViewCreationalContext;

    @Override
    public String getViewName(String viewAndParameters) {
        LOG().log(Level.INFO,
                "Attempting to retrieve view name from string \"{0}\"",
                viewAndParameters);

        String name = parseViewName(viewAndParameters);

        Bean<?> viewBean = getViewBean(name);

        if (viewBean == null) {
            return null;
        }

        if (isUserHavingAccessToView(viewBean)) {
            if (viewBean.getBeanClass().isAnnotationPresent(VaadinView.class)) {
                String specifiedViewName = viewBean.getBeanClass()
                        .getAnnotation(VaadinView.class).value();
                if (!specifiedViewName.isEmpty()) {
                    return specifiedViewName;
                }
            }
            return name;
        } else {
            LOG().log(Level.INFO,
                    "User {0} did not have access to view \"{1}\"",
                    new Object[] { JaasTools.getPrincipalName(), viewBean });
        }

        return null;
    }

    private boolean isUserHavingAccessToView(Bean<?> viewBean) {

        if (viewBean.getBeanClass().isAnnotationPresent(VaadinView.class)) {
            VaadinView viewAnnotation = viewBean.getBeanClass().getAnnotation(
                    VaadinView.class);

            if (viewAnnotation.rolesAllowed().length == 0) {
                // No roles defined, everyone is allowed
                return true;
            } else {
                boolean hasAccess = JaasTools.isUserInSomeRole(viewAnnotation
                        .rolesAllowed());

                LOG().log(
                        Level.INFO,
                        "Checking if user {0} is having access to {1}: {2}",
                        new Object[] { JaasTools.getPrincipalName(), viewBean,
                                Boolean.toString(hasAccess) });

                return hasAccess;
            }
        }

        // No annotation defined, everyone is allowed
        return true;
    }

    private Bean<?> getViewBean(String viewName) {
        LOG().log(Level.INFO, "Looking for view with name \"{0}\"", viewName);
        Set<Bean<?>> matching = new HashSet<Bean<?>>();
        Set<Bean<?>> all = beanManager.getBeans(View.class,
                new AnnotationLiteral<Any>() {
                });
        if (all.isEmpty()) {
            LOG().severe(
                    "No Views found! Please add at least one class implemeting the View interface.");
            return null;
        }
        for (Bean<?> bean : all) {
            Class<?> beanClass = bean.getBeanClass();
            VaadinView viewAnnotation = beanClass
                    .getAnnotation(VaadinView.class);
            String mapping = null;
            if (viewAnnotation != null) {
                mapping = viewAnnotation.value();
                LOG().log(Level.INFO,
                        "{0} is annotated, the viewName is \"{1}\"",
                        new Object[] { beanClass.getName(), mapping });
            }
            if (viewAnnotation == null || mapping == null || mapping.isEmpty()) {
                mapping = Conventions.deriveMappingForView(beanClass);
                LOG().log(Level.INFO,
                        "No viewName for view {0} found, using \"{1}\"",
                        new Object[] { beanClass.getName(), mapping });
            }
            if (viewAnnotation != null && viewAnnotation.supportsParameters()
                    && viewName.startsWith(mapping)) {
                matching.add(bean);
                LOG().log(
                        Level.INFO,
                        "Bean {0} with viewName \"{1}\" is one alternative for viewAndParameters \"{2}\"",
                        new Object[] { bean, mapping, viewName });
            } else if (viewName.equals(mapping)) {
                matching.add(bean);
                LOG().log(Level.INFO,
                        "Bean {0} with viewName \"{1}\" is one alternative",
                        new Object[] { bean, mapping });
            }
        }

        Set<Bean<?>> viewBeansForThisProvider = getViewBeansForCurrentUI(matching);
        if (viewBeansForThisProvider.isEmpty()) {
            LOG().log(Level.INFO, "No view beans found for current UI");
            return null;
        }

        if (viewBeansForThisProvider.size() > 1) {
            throw new RuntimeException(
                    "Multiple views mapped with same name for same UI");
        }

        return viewBeansForThisProvider.iterator().next();
    }

    private Set<Bean<?>> getViewBeansForCurrentUI(Set<Bean<?>> beans) {
        Set<Bean<?>> viewBeans = new HashSet<Bean<?>>();

        for (Bean<?> bean : beans) {
            VaadinView viewAnnotation = bean.getBeanClass().getAnnotation(
                    VaadinView.class);

            if (viewAnnotation == null || viewAnnotation.ui().equals(UI.class)) {
                viewBeans.add(bean);
                continue;
            }

            if (UI.getCurrent().getClass().equals(viewAnnotation.ui())) {
                viewBeans.add(bean);
            }
        }

        return viewBeans;
    }

    @Override
    public View getView(String viewName) {
        LOG().log(Level.INFO, "Attempting to retrieve view with name \"{0}\"",
                viewName);
        Bean<?> viewBean = getViewBean(viewName);

        if (viewBean != null) {
            if (!isUserHavingAccessToView(viewBean)) {
                LOG().log(Level.INFO,
                        "User {0} did not have access to view {1}",
                        new Object[] { JaasTools.getPrincipalName(), viewBean });
                return null;
            }

            if (currentViewCreationalContext != null) {
                LOG().log(Level.INFO,
                        "Releasing creational context for current view {0}",
                        currentViewCreationalContext);
                currentViewCreationalContext.release();
            }

            currentViewCreationalContext = beanManager
                    .createCreationalContext(viewBean);
            LOG().log(Level.INFO,
                    "Created new creational context for current view {0}",
                    currentViewCreationalContext);
            View view = (View) beanManager.getReference(viewBean,
                    viewBean.getBeanClass(), currentViewCreationalContext);
            LOG().log(Level.INFO, "Returning view instance {0}", view);
            return view;
        }

        throw new RuntimeException("Unable to instantiate view");
    }

    @PreDestroy
    protected void destroy() {
        if (currentViewCreationalContext != null) {
            LOG().log(
                    Level.INFO,
                    "CDIViewProvider is being destroyed, releasing creational context for current view");
            currentViewCreationalContext.release();
        }
    }

    private String parseViewName(String viewAndParameters) {

        String viewName = viewAndParameters;
        if (viewName.startsWith("!")) {
            viewName = viewName.substring(1);
        }
        
        if (viewName.contains("/")) {
            viewName = viewName.split("/")[0];
        }

        return viewName;
    }

    private static Logger LOG() {
        return Logger.getLogger(CDIViewProvider.class.getCanonicalName());
    }
}
