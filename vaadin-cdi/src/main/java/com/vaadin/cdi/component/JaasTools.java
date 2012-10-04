package com.vaadin.cdi.component;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class JaasTools {

    @Inject
    private HttpServletRequest request;

    public void login(String username, String password) throws ServletException {
        request.login(username, password);
    }

    public void logout() throws ServletException {
        request.logout();
    }

    public boolean isUserSignedIn() {
        Principal principal = request.getUserPrincipal();
        return principal != null;
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public String getPrincipalName() {
        return request.getUserPrincipal().getName();
    }

    public boolean isUserInSomeRole(String... roles) {
        for (String role : roles) {
            if (isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }
}
