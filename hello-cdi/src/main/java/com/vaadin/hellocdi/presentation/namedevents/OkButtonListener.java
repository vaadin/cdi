package com.vaadin.hellocdi.presentation.namedevents;

import javax.enterprise.event.Observes;
import javax.inject.Named;

import com.vaadin.cdi.viewscope.VaadinViewScoped;

@VaadinViewScoped
public class OkButtonListener {

    public void onOkButtonClicked(@Observes @Named("ok") String message){
        System.out.println("Received: " + message);
    }
}
