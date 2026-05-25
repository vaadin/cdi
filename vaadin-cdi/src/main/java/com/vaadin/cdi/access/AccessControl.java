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

import jakarta.enterprise.inject.Alternative;

/**
 * Access control base class.
 * 
 * Various implementations can exist (default JAAS, others with
 * {@link Alternative}) and the active implementation can be selected in
 * beans.xml .
 */
public abstract class AccessControl {
    /**
     * Returns true if some used has logged in.
     * 
     * @return true if a user is logged in
     */
    public abstract boolean isUserSignedIn();

    /**
     * Checks if the current user has a role.
     * 
     * @param role
     * @return true if currently logged in user is in given role
     */
    public abstract boolean isUserInRole(String role);

    /**
     * Returns the principal (user) name of the currently logged in user.
     * 
     * @return name of the user that is currently logged in, if no user is
     *         logged in null will be returned.
     */
    public abstract String getPrincipalName();

    /**
     * Checks if the user has any of the given roles.
     * 
     * @param roles
     * @return true if currently logged in user is in some of given roles
     */
    public boolean isUserInSomeRole(String... roles) {
        for (String role : roles) {
            if (isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }

}
