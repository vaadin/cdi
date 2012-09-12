package com.vaadin.cdi;

import static com.vaadin.cdi.Naming.firstToLower;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.vaadin.cdi.component.JaasTools;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;
import java.util.HashSet;
import java.util.Set;

public class CDIViewProvider implements ViewProvider {

    @Inject
    private BeanManager beanManager;

    @Inject
    private JaasTools jaasTools;

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
                return jaasTools
                        .isUserInSomeRole(viewAnnotation.rolesAllowed());
            }
        }

        // No annotation defined, everyone is allowed
        return true;
    }

    private Bean<?> getViewBean(String viewName) {
        Set<Bean<?>> viewBeans = beanManager.getBeans(View.class,
                new VaadinViewAnnotation(viewName));
        //TODO conventional lookup
        Set<Bean<?>> viewBeansForThisProvider = getViewBeansForCurrentUI(viewBeans);

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

            if (viewAnnotation.ui().equals(UI.class)) {
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
            View view = (View) beanManager.getReference(viewBean,
                    viewBean.getBeanClass(),
                    beanManager.createCreationalContext(viewBean));

            return view;
        }

        throw new RuntimeException("Unable to instantiate view");
    }

    private String parseViewName(String viewAndParameters) {
        if (viewAndParameters.startsWith("!")) {
            return viewAndParameters.substring(1);
        }

        return viewAndParameters;
    }

    String evaluateViewName(View view) {
        Class<? extends View> clazz = view.getClass();
        VaadinView annotation = clazz.getAnnotation(VaadinView.class);
        String configuredViewName = annotation.value();
        if (configuredViewName.isEmpty()) {
            return firstToLower(clazz.getSimpleName());
        } else {
            return configuredViewName;
        }
    }

    private static Logger LOG() {
        return Logger.getLogger(CDIViewProvider.class.getCanonicalName());
    }
}
