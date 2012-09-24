package com.vaadin.hellocdi.presentation.namedevents;

import javax.enterprise.event.Observes;
import javax.inject.Named;

import com.vaadin.cdi.VaadinUI;

@VaadinUI
public class OkButtonListener {

    public OkButtonListener() {
        System.out.println("instantiating new " + this);
    }

    public void onOkButtonClicked(@Observes
    @Named("ok")
    String message) {
        System.out.println("Received: " + message);
    }
}
