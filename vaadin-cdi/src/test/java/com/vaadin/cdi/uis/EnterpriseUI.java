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
import com.vaadin.cdi.internal.Counter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@CDIUI(value = "enterpriseUI")
public class EnterpriseUI extends UI {

    public static final String CONSTRUCT_COUNT = "EnterpriseUIConstruct";
    private int clickCount;

    @Inject
    Boundary boundary;
    
    @Inject
    private EnterpriseLabel injectedLabel;

    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        clickCount = 0;
        counter.increment(CONSTRUCT_COUNT);
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+EnterpriseUI");
        label.setId("label");
        Button button = new Button("InvokeEJB", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String response = boundary.echo(String.valueOf(++clickCount));
                label.setValue(response);
            }
        });
        button.setId("button");

        layout.addComponent(label);
        layout.addComponent(button);
        layout.addComponent(injectedLabel);
        setContent(layout);
    }

}
