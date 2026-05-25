/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.uis;

import jakarta.ejb.Stateless;

/**
 */
@Stateless
public class Boundary {

    public String echo(String message) {
        return "Echo: " + message;
    }
}
