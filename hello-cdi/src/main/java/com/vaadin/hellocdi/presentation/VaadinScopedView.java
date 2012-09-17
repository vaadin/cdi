package com.vaadin.hellocdi.presentation;

import com.vaadin.cdi.VaadinView;
import com.vaadin.cdi.viewscope.VaadinViewScoped;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@VaadinView
@VaadinViewScoped
public class VaadinScopedView implements View{

    @Override
    public void enter(ViewChangeEvent event) {
        System.out.println("DependentView entered!");
    }

}
