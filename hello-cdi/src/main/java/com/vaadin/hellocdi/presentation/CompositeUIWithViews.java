package com.vaadin.hellocdi.presentation;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinView;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinUI
public class CompositeUIWithViews extends UI{
    
    @Inject @VaadinView
    DependentView dependentView;
    
    @Inject @VaadinView
    VaadinScopedView scopedView;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(new Label("+CompositeUI works " + dependentView + " " + scopedView));
        setContent(layout);
        
    }

}
