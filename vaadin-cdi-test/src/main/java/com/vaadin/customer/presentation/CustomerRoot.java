package com.vaadin.customer.presentation;

import javax.inject.Inject;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.SimpleViewDisplay;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@VaadinUI
public class CustomerRoot extends UI {

    private Navigator navigator;

    @Inject
    private CDIViewProvider cdiViewProvider;

    @Override
    protected void init(VaadinRequest request) {
        SimpleViewDisplay viewDisplay = new SimpleViewDisplay();
        setContent(viewDisplay);

        navigator = new Navigator(this, (ViewDisplay) viewDisplay);
        navigator.addProvider(cdiViewProvider);
    }
}
