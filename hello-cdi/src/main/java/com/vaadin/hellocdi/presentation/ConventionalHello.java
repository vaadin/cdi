package com.vaadin.hellocdi.presentation;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author adam-bien.com
 */
@VaadinUI
public class ConventionalHello extends UI {
    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(new Label("+UI with default value"));
        setContent(layout);
    }

}
