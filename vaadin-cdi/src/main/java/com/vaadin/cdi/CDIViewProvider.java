package com.vaadin.cdi;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Instance<View> allViews = views.select(new VaadinViewAnnotation());
        if (allViews.isUnsatisfied()) {
            return null;
        }
        for (View view : allViews) {
            if (viewName.equals(evaluateViewName(view))) {
                result.add(view);
            }
        }
        
        if (result.size() > 1) {
            throw new RuntimeException(
                    "CDIViewProvider has multiple choises for view with name "
                    + viewName);
        }

        return result.get(0);
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
            return clazz.getSimpleName();
        } else {
            return configuredViewName;
        }
    }
}
