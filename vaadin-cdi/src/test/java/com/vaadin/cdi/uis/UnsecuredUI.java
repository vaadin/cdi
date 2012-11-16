package com.vaadin.cdi.uis;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.component.ComponentTools;
import com.vaadin.cdi.component.JaasTools;
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
