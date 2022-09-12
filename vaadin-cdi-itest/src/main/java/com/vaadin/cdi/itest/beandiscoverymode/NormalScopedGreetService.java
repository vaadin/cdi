package com.vaadin.cdi.itest.beandiscoverymode;

import com.vaadin.cdi.annotation.NormalUIScoped;

@NormalUIScoped
public class NormalScopedGreetService {

    public String greet(String name) {
        String greeting = "Hello %s from NormalScoped service.";
        if (name == null || name.isEmpty()) {
            return String.format(greeting, "anonymous user");
        } else {
            return String.format(greeting, name);
        }
    }
}
