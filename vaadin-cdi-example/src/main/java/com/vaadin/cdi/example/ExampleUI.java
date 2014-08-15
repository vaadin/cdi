package com.vaadin.cdi.example;

import javax.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.example.util.CounterService;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI("")
public class ExampleUI extends UI {

    @Inject
    private CDIViewProvider viewProvider;

    @Inject
    private CounterService counter;

    @Override
    public void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout navigatorLayout = new VerticalLayout();
        navigatorLayout.setSizeFull();

        Navigator navigator = new Navigator(ExampleUI.this, navigatorLayout);
        navigator.addProvider(viewProvider);

        setContent(navigatorLayout);
    }

}
