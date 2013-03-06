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

package com.vaadin.cdi.uis;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.access.ComponentTools;
import com.vaadin.cdi.access.JaasTools;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinUI
public class UnsecuredUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+UnsecuredUI");
        label.setId("label");

        Label principalName = new Label(JaasTools.getPrincipalName());
        principalName.setId("principalName");

        Label isUserInRole = new Label(String.valueOf(JaasTools
                .isUserInRole("duke")));
        isUserInRole.setId("isUserInRole");

        Label isUserInSomeRole = new Label(String.valueOf(JaasTools
                .isUserInSomeRole("duke")));
        isUserInSomeRole.setId("isUserInSomeRole");

        Label isUserSignedIn = new Label(String.valueOf(JaasTools
                .isUserInSomeRole("duke")));
        isUserSignedIn.setId("isUserSignedIn");

        Label currentRequestNotNull = new Label(String.valueOf(JaasTools
                .getCurrentRequest() != null));
        currentRequestNotNull.setId("currentRequestNotNull");

        final Label disabled = new Label("DisabledLabel");
        disabled.setId("disabled");
        ComponentTools.setEnabledForRoles(disabled, "duke");
        final Label invisible = new Label("InvisibleLabel");
        invisible.setId("invisible");
        ComponentTools.setVisibleForRoles(invisible, "duke");

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

}
