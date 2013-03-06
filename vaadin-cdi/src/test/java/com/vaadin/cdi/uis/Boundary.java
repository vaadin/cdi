package com.vaadin.cdi.uis;

import javax.ejb.Stateless;

/**
 * @author: adam-bien.com
 */
@Stateless
public class Boundary {

    public String echo(String message) {
        return "Echo: " + message;
    }
}
