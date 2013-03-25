/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.cdi.access;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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

    @Produces
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
