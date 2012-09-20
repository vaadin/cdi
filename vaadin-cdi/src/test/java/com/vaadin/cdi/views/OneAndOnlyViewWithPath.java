package com.vaadin.cdi.views;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 *
 * @author adam-bien.com
 */
@VaadinView("custom")
public class OneAndOnlyViewWithPath implements View{

    @Override
    public void enter(ViewChangeEvent event) {
    }
    
}
