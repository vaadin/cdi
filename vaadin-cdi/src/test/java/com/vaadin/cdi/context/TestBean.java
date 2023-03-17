/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBean {
    private static AtomicInteger beanCount = new AtomicInteger();
    private String state = "";

    @PostConstruct
    private void construct() {
        beanCount.incrementAndGet();
    }

    @PreDestroy
    private void destruct() {
        beanCount.decrementAndGet();
    }

    public static int getBeanCount() {
        return beanCount.get();
    }

    public static void resetCount() {
        beanCount.set(0);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
