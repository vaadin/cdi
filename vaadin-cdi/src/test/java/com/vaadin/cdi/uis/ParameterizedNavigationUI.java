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

import com.vaadin.cdi.CDINavigator;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@CDIUI(value = "parameterizedNavigationUI")
public class ParameterizedNavigationUI extends UI {

    public static final String CONSTRUCT_COUNT = "ParameterizedNavigationUIConstruct";
    @Inject
    CDINavigator navigator;

    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
    }

    @Override
    protected void init(VaadinRequest request) {
        final String navigateTo = request.getParameter("navigateTo");
        setSizeFull();

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+SecondUI");
        label.setId("label");
        Button navigate = new Button("button", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.init(ParameterizedNavigationUI.this, layout);
                navigator.navigateTo(navigateTo);
            }
        });
        navigate.setId("navigate");
        layout.addComponent(label);
        layout.addComponent(navigate);
        setContent(layout);
    }

    public static String getNavigateToParam(String navigateTo) {
        return "?navigateTo="+navigateTo;
    }

}
