/*
 * Copyright 2012 Vaadin Ltd.
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

import com.vaadin.ui.Component;

/**
 * 
 * ComponentTools is JaasTools addition that allows enabling/disabling, and
 * hiding Vaadin component based on roles available through JAAS. In order to
 * use these methods user must be signed in to container managed security
 * domain.
 */
public class ComponentTools {

    /**
     * Sets given component enabled if currently signed in user is in one or
     * more given roles, otherwise component is disabled.
     * 
     * @param component
     * @param roles
     */
    public static void setEnabledForRoles(Component component, String... roles) {
        component.setEnabled(JaasTools.isUserInSomeRole(roles));
    }

    /**
     * Sets given component visible if currently signed in user is in one or
     * more given roles, otherwise component is hidden.
     * 
     * @param component
     * @param roles
     */
    public static void setVisibleForRoles(Component component, String... roles) {
        component.setVisible(JaasTools.isUserInSomeRole(roles));
    }
}
