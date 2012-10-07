package com.vaadin.cdi.uis;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class DependentCDIEventListener{

    private final static AtomicInteger EVENT_COUNTER = new AtomicInteger(0);
    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }


    public void onEventArrival(@Observes String message) {
        EVENT_COUNTER.incrementAndGet();
        System.out.println("+DependentCDIEventListener Message arrived!");
    }

    public static int getNumberOfDeliveredEvents() {
        return EVENT_COUNTER.get();
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }


    public static void resetCounter() {
        COUNTER.set(0);
    }

    public static void resetEventCounter() {
        EVENT_COUNTER.set(0);
    }


}