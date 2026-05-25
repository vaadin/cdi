/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.access;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServletService;

/**
 * JaasAccessControl is an {@link AccessControl} implementation that also
 * provides set of JAAS helper methods to login and out the user as well as to
 * query additional information.
 * 
 * In order to use JaasAccessControl a proper security domain must be configured
 * in the underlying application server.
 */
@Default
public class JaasAccessControl extends AccessControl implements Serializable {

    @Override
    public boolean isUserSignedIn() {
        Principal principal = getCurrentRequest().getUserPrincipal();
        return principal != null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return getCurrentRequest().isUserInRole(role);
    }

    @Override
    public String getPrincipalName() {
        Principal principal = getCurrentRequest().getUserPrincipal();
        if (principal != null) {
            return principal.getName();
        }

        return null;
    }

    /**
     * Logs in the user to underlying container security context using
     * configured security domain in deployment descriptor
     * 
     * @param username
     * @param password
     * @throws ServletException
     *             if login fails or current session has already been
     *             authenticated
     */
    public static void login(String username, String password)
            throws ServletException {
        getCurrentRequest().login(username, password);
    }

    /**
     * Logs user out from current container managed security context
     * 
     * @throws ServletException
     */
    public static void logout() throws ServletException {
        getCurrentRequest().logout();
    }

    @RequestScoped
    public static HttpServletRequest getCurrentRequest() {
        HttpServletRequest request = VaadinServletService
                .getCurrentServletRequest();
        getLogger().info("Getting request " + request);

        return request;
    }

    private static Logger getLogger() {
        return Logger.getLogger(JaasAccessControl.class.getCanonicalName());
    }
}
