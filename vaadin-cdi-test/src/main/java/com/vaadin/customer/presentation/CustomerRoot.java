package com.vaadin.customer.presentation;

import javax.inject.Inject;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.SimpleViewDisplay;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.UI;

@VaadinUI(mapping = "customers")
public class CustomerRoot extends UI {

    private Navigator navigator;

    @Inject
    private CDIViewProvider cdiViewProvider;

    @Override
    protected void init(WrappedRequest request) {
        SimpleViewDisplay viewDisplay = new SimpleViewDisplay();
        setContent(viewDisplay);

        navigator = new Navigator(getPage(), viewDisplay);
        navigator.addProvider(cdiViewProvider);
    }
}
