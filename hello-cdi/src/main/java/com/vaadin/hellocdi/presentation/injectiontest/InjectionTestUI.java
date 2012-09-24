package com.vaadin.hellocdi.presentation.injectiontest;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@VaadinUI
public class InjectionTestUI extends UI {

    @Inject
    private InjectableComponent injectedComponent;

    @Override
    protected void init(VaadinRequest request) {
        addComponent(injectedComponent);
    }
}
