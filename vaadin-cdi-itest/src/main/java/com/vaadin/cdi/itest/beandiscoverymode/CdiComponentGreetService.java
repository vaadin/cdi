package com.vaadin.cdi.itest.beandiscoverymode;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.annotation.UIScoped;

@CdiComponent
@UIScoped
public class CdiComponentGreetService {

    public String greet(String name) {
        String greeting = "Hello %s from CDIComponent service.";
        if (name == null || name.isEmpty()) {
            return String.format(greeting, "anonymous user");
        } else {
            return String.format(greeting, name);
        }
    }
}
