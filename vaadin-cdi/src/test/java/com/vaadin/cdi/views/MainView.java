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
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@CDIView(value=MainView.VIEW_ID)
public class MainView extends AbstractScopedInstancesView {
    public static final String VIEW_ID = "main";
    private static final String DESCRIPTION_LABEL = "label";
    
    @Override
    protected Component buildContent() {
        Label label = new Label(VIEW_ID);
        label.setId(DESCRIPTION_LABEL);
        return label;
    }
    
}