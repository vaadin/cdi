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

import jakarta.enterprise.inject.Typed;
import java.util.concurrent.atomic.AtomicLong;

@Typed()
public class ProducedBean {
    private static final AtomicLong counter = new AtomicLong(0);
    private final long id = counter.incrementAndGet();
    private final String key;
    
    public ProducedBean() {
        key = "direct inject";
    }
    
    public ProducedBean(String key) {
        this.key = key;
    }
    
    public String getId() {
        return key+"/"+id;
    }
}
