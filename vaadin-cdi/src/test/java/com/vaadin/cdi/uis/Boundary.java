package com.vaadin.cdi.uis;

import javax.ejb.Stateless;

/**
 */
@Stateless
public class Boundary {

    public String echo(String message) {
        return "Echo: " + message;
    }
}
