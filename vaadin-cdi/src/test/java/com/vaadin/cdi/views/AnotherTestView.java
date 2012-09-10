package com.vaadin.cdi.views;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 *
 * @author adam-bien.com
 */
@VaadinView("TestView")
public class AnotherTestView implements View{

    @Override
    public void enter(ViewChangeEvent event) {
        System.out.println("I'm changed");
    }
    
}
