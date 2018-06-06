package com.vaadin.cdi.itest;

import javax.enterprise.context.ApplicationScoped;
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
