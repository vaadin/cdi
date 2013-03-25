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

package com.vaadin.cdi.uis;

import javax.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.access.AccessControl;
import com.vaadin.cdi.access.JaasAccessControl;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI(value = "unsecuredUI")
public class UnsecuredUI extends UI {

    @Inject
    private AccessControl accessControl;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+UnsecuredUI");
        label.setId("label");

        Label principalName = new Label(accessControl.getPrincipalName());
        principalName.setId("principalName");

        Label isUserInRole = new Label(String.valueOf(accessControl
                .isUserInRole("duke")));
        isUserInRole.setId("isUserInRole");

        Label isUserInSomeRole = new Label(String.valueOf(accessControl
                .isUserInSomeRole("duke")));
        isUserInSomeRole.setId("isUserInSomeRole");

        Label isUserSignedIn = new Label(String.valueOf(accessControl
                .isUserInSomeRole("duke")));
        isUserSignedIn.setId("isUserSignedIn");

        // specific to JAAS
        Label currentRequestNotNull = new Label(
                String.valueOf(JaasAccessControl.getCurrentRequest() != null));
        currentRequestNotNull.setId("currentRequestNotNull");

        final Label disabled = new Label("DisabledLabel");
        disabled.setId("disabled");
        setEnabledForRoles(disabled, "duke");
        final Label invisible = new Label("InvisibleLabel");
        invisible.setId("invisible");
        setVisibleForRoles(invisible, "duke");

        layout.addComponent(label);
        layout.addComponent(principalName);
        layout.addComponent(isUserInRole);
        layout.addComponent(isUserInSomeRole);
        layout.addComponent(currentRequestNotNull);
        layout.addComponent(isUserSignedIn);
        layout.addComponent(disabled);
        layout.addComponent(invisible);
        setContent(layout);
    }

    /**
     * Sets given component enabled if currently signed in user is in one or
     * more given roles, otherwise component is disabled.
     * 
     * This method might be moved out of {@link AccessControlUtil} in future
     * versions.
     * 
     * @param component
     * @param roles
     */
    public void setEnabledForRoles(Component component, String... roles) {
        component.setEnabled(accessControl.isUserInSomeRole(roles));
    }

    /**
     * Sets given component visible if currently signed in user is in one or
     * more given roles, otherwise component is hidden.
     * 
     * This method might be moved out of {@link AccessControlUtil} in future
     * versions.
     * 
     * @param component
     * @param roles
     */
    public void setVisibleForRoles(Component component, String... roles) {
        component.setVisible(accessControl.isUserInSomeRole(roles));
    }

}
