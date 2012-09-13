package com.vaadin.hellocdi.presentation;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.cdi.VaadinView;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinUI
@VaadinUIScoped
public class CompositeUIWithViews extends UI{
    
    @Inject @VaadinView
    DependentView dependentView;

    @Override
    protected void init(WrappedRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(new Label("+CompositeUI works"));
        setContent(layout);
        
    }

}
