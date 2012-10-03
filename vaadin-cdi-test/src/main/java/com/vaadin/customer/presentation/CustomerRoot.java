package com.vaadin.customer.presentation;

import javax.inject.Inject;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.SimpleViewDisplay;
<<<<<<< HEAD
=======
import com.vaadin.navigator.ViewDisplay;
>>>>>>> 15c69a7b8c7a52fc35d72b22854c18995155bead
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
