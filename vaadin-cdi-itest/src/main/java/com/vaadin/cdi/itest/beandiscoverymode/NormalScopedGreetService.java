package com.vaadin.cdi.itest.beandiscoverymode;

import com.vaadin.cdi.annotation.NormalUIScoped;

@NormalUIScoped
public class NormalScopedGreetService {

    public String greet(String name) {
        if (name == null || name.isEmpty()) {
            return "Hello anonymous user";
        } else {
            return "Hello " + name;
        }
    }
}