/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi.uis;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;

public class DependentCDIEventListener implements Serializable {

    private final static AtomicInteger EVENT_COUNTER = new AtomicInteger(0);
    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    public void onEventArrival(@Observes
    String message) {
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