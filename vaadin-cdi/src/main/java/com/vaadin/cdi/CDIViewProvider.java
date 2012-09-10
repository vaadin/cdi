package com.vaadin.cdi;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

public class CDIViewProvider implements ViewProvider {

    @Inject
    @Any
    private Instance<View> views;

    @Override
    public String getViewName(String viewAndParameters) {
        String name = parseViewName(viewAndParameters);
        getView(name); // are checks necessary here?
        return name;
    }

    @Override
    public View getView(String viewName) {
        List<View> result = new ArrayList<View>();
        Instance<View> allViews = views.select(new VaadinViewAnnotation(
                viewName));
        View configuredView = null;

        if (!allViews.isUnsatisfied() && !allViews.isAmbiguous()) {
            configuredView = allViews.get();
            LOG().info("View with name: " + viewName + " uniquely configured");
        }
        for (View view : allViews) {
            if (view != configuredView
                    && viewName.equals(evaluateViewName(view))) {
                result.add(view);
                LOG().info(
                        "Another view with conflicting name found: "
                                + view.getClass());
            }
        }

        if ((configuredView == null && result.size() > 1)
                || (configuredView != null && !result.isEmpty())) {
            String viewNames = "";
            for (View view : allViews) {
                Class<? extends View> clazz = view.getClass();
                String className = clazz.getName();
                VaadinView vaadinView = clazz.getAnnotation(VaadinView.class);
                String annotationValue = vaadinView.value();
                viewNames += "@VaadinView(" + annotationValue + ") class "
                        + className + "\n";
            }
            String message = "CDIViewProvider has multiple choices "
                    + viewNames + " for view with name " + viewName;
            LOG().warning(message);
            throw new IllegalStateException(message);
        }

        if (configuredView != null) {
            return configuredView;
        } else if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }

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
            return normalize(clazz.getSimpleName());
        } else {
            return configuredViewName;
        }
    }

    String normalize(String name) {
        char firstLower = Character.toLowerCase(name.charAt(0));
        if (name.length() > 1) {
            return firstLower + name.substring(1);
        } else {
            return String.valueOf(firstLower);
        }
    }

    private static Logger LOG() {
        return Logger.getLogger(CDIViewProvider.class.getCanonicalName());
    }
}
