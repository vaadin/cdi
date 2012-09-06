package com.vaadin.cdi;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 *
 * @author adam-bien.com
 */
@VaadinView("TestView")
public class TestView implements View{

    @Override
    public void enter(ViewChangeEvent event) {
        System.out.println("I'm changed");
    }
    
}
