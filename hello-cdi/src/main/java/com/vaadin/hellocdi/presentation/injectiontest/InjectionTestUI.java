package com.vaadin.hellocdi.presentation.injectiontest;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

@VaadinUI
@VaadinUIScoped
public class InjectionTestUI extends UI {

    @Inject
    private InjectableComponent injectedComponent;

    @Override
    protected void init(WrappedRequest request) {
        addComponent(injectedComponent);
    }
}
