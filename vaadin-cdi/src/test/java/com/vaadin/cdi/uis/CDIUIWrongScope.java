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
package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@CDIUI
@NormalUIScoped
public class CDIUIWrongScope extends UI {
    @Override
    protected void init(VaadinRequest request) {

    }
}
