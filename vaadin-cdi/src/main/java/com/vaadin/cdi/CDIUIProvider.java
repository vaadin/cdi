package com.vaadin.cdi;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.Application;
import com.vaadin.UIRequiresMoreInformationException;
import com.vaadin.terminal.UIProvider;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.UI;

public class CDIUIProvider implements UIProvider {

    @Inject
    @Any
    private Instance<UI> UIs;

    @Override
    public Class<? extends UI> getUIClass(Application application,
            WrappedRequest request) throws UIRequiresMoreInformationException {
        String UIMapping = parseUIMapping(request);
        UI ui = selectUIMatchingAnnotation(new VaadinUIAnnotation(UIMapping));

        if (ui != null) {
            return ui.getClass();
        }

        return null;
    }

    @Override
    public UI instantiateUI(Application application, Class<? extends UI> type,
            WrappedRequest request) {
        String UIMapping = parseUIMapping(request);
        UI ui = selectUIMatchingAnnotation(new VaadinUIAnnotation(UIMapping));

        if (ui != null) {
            return ui;
        }

        throw new RuntimeException("Could not instantiate root");
    }

    private UI selectUIMatchingAnnotation(VaadinUI vaadinUI) {
        Instance<UI> selectedUI = UIs.select(vaadinUI);

        if (selectedUI.isUnsatisfied()) {
            System.out.println("Could not find ui");
            return null;
        }

        if (selectedUI.isAmbiguous()) {
            System.out.println("Ambiguous ui definition");
            return null;
        }

        return selectedUI.get();
    }

    private String parseUIMapping(WrappedRequest request) {
        String requestPath = request.getRequestPathInfo();
        if (requestPath != null && requestPath.length() > 1) {
            if (requestPath.endsWith("/")) {
                return requestPath.substring(1, requestPath.lastIndexOf("/"));
            } else {
                return requestPath.substring(1);
            }
        }
        return "";
    }
}
