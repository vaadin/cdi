/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class AbstractCountedBean implements CountedPerUI {

    private String data = "";

    @PostConstruct
    private void construct() {
        countConstruct();
    }

    @PreDestroy
    private void destroy() {
        countDestroy();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
