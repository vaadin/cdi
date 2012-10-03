package com.vaadin.cdi.component;

import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.Component;

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
        return VaadinServletService.getCurrentServletRequest();
    }
}
