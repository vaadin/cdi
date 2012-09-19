package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

@VaadinUI
@VaadinUIScoped
public class EmptyUI extends UI {

    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();

    }

    @Override
    protected void init(WrappedRequest request) {
        // TODO Auto-generated method stub

    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }

}
