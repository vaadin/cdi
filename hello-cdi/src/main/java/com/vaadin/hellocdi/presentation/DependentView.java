package com.vaadin.hellocdi.presentation;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@VaadinView
public class DependentView implements View{

    @Override
    public void enter(ViewChangeEvent event) {
        System.out.println("DependentView entered!");
    }
    
    

}
