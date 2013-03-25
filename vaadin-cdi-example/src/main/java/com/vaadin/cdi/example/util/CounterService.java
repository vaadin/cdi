package com.vaadin.cdi.example.util;

import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.cdi.UIScoped;

@UIScoped
public class CounterService {

    private final AtomicInteger COUNTER = new AtomicInteger(0);

    public int next() {
        return COUNTER.incrementAndGet();
    }

    public int get() {
        return COUNTER.get();
    }

}
