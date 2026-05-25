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

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 */
@CDIView(value = "instrumentedView")
public class InstrumentedView extends CustomComponent implements View {

    public static final String CONSTRUCT_COUNT = "InstrumentedViewConstruct";
    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);

    }

    @Override
    public void enter(ViewChangeEvent event) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);
        Label label = new Label("ViewLabel");
        label.setId("view");
        layout.addComponent(label);
    }

}
