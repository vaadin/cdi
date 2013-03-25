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

import javax.enterprise.inject.Alternative;

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
