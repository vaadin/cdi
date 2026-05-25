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

import com.vaadin.cdi.internal.Counter;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.io.Serializable;

public class DependentCDIEventListener implements Serializable {

    public static final String CONSTRUCT_COUNT = "DependentCDIEventListenerConstruct";
    public static final String EVENT_COUNT = "DependentCDIEventListenerEvent";

    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
    }


    public void onEventArrival(@Observes String message) {
        counter.increment(EVENT_COUNT);
        System.out.println("+DependentCDIEventListener Message arrived!");
    }


}