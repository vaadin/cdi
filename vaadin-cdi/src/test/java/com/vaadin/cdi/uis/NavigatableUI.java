/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDINavigator;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import jakarta.inject.Inject;


@CDIUI
public class NavigatableUI extends UI {
    
    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        navigator.init(NavigatableUI.this, this);

    }

}
