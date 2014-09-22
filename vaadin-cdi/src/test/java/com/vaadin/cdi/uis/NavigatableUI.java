package com.vaadin.cdi.uis;

import javax.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;


@CDIUI
public class NavigatableUI extends UI {
    
    @Inject
    CDIViewProvider viewProvider;
    
    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        Navigator navigator = new Navigator(NavigatableUI.this, this);
        navigator.addProvider(viewProvider);

    }

}
