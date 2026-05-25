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

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class Counter {
    ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();

    public int increment(String key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        return map.get(key).incrementAndGet();
    }

    public int get(String key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        return map.get(key).get();
    }

    public void reset() {
        map.clear();
    }
}
