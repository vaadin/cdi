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

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@CDIUI(value = "uIWithCDIDependentListener")
public class UIWithCDIDependentListener extends UI {

    public static final String CONSTRUCT_COUNT = "UIWithCDIDependentListenerConstruct";
    @Inject
    private jakarta.enterprise.event.Event<String> events;
    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+UIWithCDIDependentListener");
        label.setId("label");
        Button button = new Button("button", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                events.fire("Fired: " + (System.currentTimeMillis()));
            }
        });
        button.setId("button");
        layout.addComponent(label);
        layout.addComponent(button);
        setContent(layout);
    }

}
