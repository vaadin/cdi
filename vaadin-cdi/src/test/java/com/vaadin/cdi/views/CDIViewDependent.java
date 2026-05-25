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
package com.vaadin.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;

import jakarta.enterprise.context.Dependent;

@CDIView("viewDependent")
@Dependent
public class CDIViewDependent implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
