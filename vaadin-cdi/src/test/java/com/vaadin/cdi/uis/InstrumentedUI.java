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
import com.vaadin.cdi.internal.Counter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@CDIUI(value = "instrumentedUI")
public class InstrumentedUI extends UI {

    public static final String CONSTRUCT_COUNT = "InstrumentedUIConstruct";

    @Inject
    CDINavigator navigator;

    @Inject
    Counter counter;

    private int clickCount;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+InstrumentedUI");
        label.setId("label");
        Button button = new Button("Change Label", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                label.setValue(String.valueOf(++clickCount));
            }
        });
        button.setId("button");
        Button navigate = new Button("Navigate", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.init(InstrumentedUI.this, layout);
                navigator.navigateTo("instrumentedView");
            }
        });
        navigate.setId("navigate");
        layout.addComponent(label);
        layout.addComponent(button);
        layout.addComponent(navigate);
        setContent(layout);
    }

}
