package com.vaadin.cdi.component;

import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Component;
import com.vaadin.util.CurrentInstance;

public class ComponentTools {

    public void setEnabledForRoles(Component component, String... roles) {
        component.setEnabled(isUserInSomeGivenRole(roles));
    }

    public void setVisibleForRoles(Component component, String... roles) {
        component.setVisible(isUserInSomeGivenRole(roles));
    }

    private boolean isUserInSomeGivenRole(String... roles) {
        HttpServletRequest request = getCurrentRequest();

        for (String role : roles) {
            if (request.isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }

    @Produces
    protected HttpServletRequest getCurrentRequest() {
        return (HttpServletRequestWrapper) CurrentInstance
                .get(WrappedRequest.class);
    }
}
