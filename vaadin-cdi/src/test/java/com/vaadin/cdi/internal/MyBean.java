/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;

@NormalUIScoped
public class MyBean {
    private static int counter = 0;

    private final int id = counter++;

    public MyBean() {
        System.out.println("Created MyBean with id " + id);
    }

    public int getBeanId() {
        return id;
    }
}
