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
package com.vaadin.cdi.shiro;

import jakarta.annotation.security.RolesAllowed;

import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@CDIView(AdminView.VIEW_ID)
@RolesAllowed("admin")
public class AdminView extends AbstractShiroTestView {

    public static final String VIEW_ID = "admin";

    @Override
    protected Component buildContent() {
        Label label = new Label(VIEW_ID);
        label.setId(LABEL_ID);
        return label;
    }

}
