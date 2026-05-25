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
package com.vaadin.cdi.internal;

import com.vaadin.cdi.ViewScoped;

// Does not implement Serializable
@ViewScoped
public class NonPassivatingBean {

    private String someString = "NonPassivatingBean" + hashCode();
    
    public String getSomeString() {
        return someString;
    }
}
