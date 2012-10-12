package com.vaadin.cdi;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.vaadin.cdi.component.JaasTools;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

public class CDIViewProvider implements ViewProvider {

    @Inject
    private BeanManager beanManager;

    @Override
    public String getViewName(String viewAndParameters) {
        String name = parseViewName(viewAndParameters);

        Bean<?> viewBean = getViewBean(name);

        if (viewBean == null) {
            return null;
        }

        if (isUserHavingAccessToView(viewBean)) {
            return name;
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
                return JaasTools
                        .isUserInSomeRole(viewAnnotation.rolesAllowed());
            }
        }

        // No annotation defined, everyone is allowed
        return true;
    }

    private Bean<?> getViewBean(String viewName) {
        Set<Bean<?>> matching = new HashSet<Bean<?>>();
        Set<Bean<?>> all = beanManager.getBeans(View.class,
                new AnnotationLiteral<Any>() {
                });
        if (all.isEmpty()) {
            LOG().severe("No Views found!");
            return null;
        }
        for (Bean<?> bean : all) {
            Class<?> beanClass = bean.getBeanClass();
            VaadinView viewAnnotation = beanClass
                    .getAnnotation(VaadinView.class);
            String mapping = null;
            if (viewAnnotation != null) {
                mapping = viewAnnotation.value();
                LOG().info(
                        beanClass.getName() + " is annotated, the value is: "
                                + mapping);
            }
            if (viewAnnotation == null || mapping == null || mapping.isEmpty()) {
                mapping = Conventions.deriveMappingForView(beanClass);
                LOG().info(
                        "No value for view " + beanClass.getName() + " found "
                                + " evaluated defaults are: " + mapping);
            }
            if (viewName.equals(mapping)) {
                matching.add(bean);
                LOG().info(
                        "Bean " + beanClass.getName() + " with computed name: "
                                + mapping + " added !");
            }
        }

        Set<Bean<?>> viewBeansForThisProvider = getViewBeansForCurrentUI(matching);
        if (viewBeansForThisProvider.isEmpty()) {
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

        Bean<?> viewBean = getViewBean(viewName);

        if (viewBean != null) {
            return (View) beanManager.getReference(viewBean,
                    viewBean.getBeanClass(),
                    beanManager.createCreationalContext(viewBean));

        }

        throw new RuntimeException("Unable to instantiate view");
    }

    private String parseViewName(String viewAndParameters) {
        if (viewAndParameters.startsWith("!")) {
            return viewAndParameters.substring(1);
        }

        return viewAndParameters;
    }

    private static Logger LOG() {
        return Logger.getLogger(CDIViewProvider.class.getCanonicalName());
    }
}
