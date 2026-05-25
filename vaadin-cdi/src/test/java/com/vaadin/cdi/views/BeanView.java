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
package com.vaadin.cdi.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import com.vaadin.cdi.internal.MyBean;
import com.vaadin.ui.Label;

public class BeanView extends Label {
    @Inject
    MyBean bean;

    @PostConstruct
    private void populate() {
        setValue("Bean " + bean.getBeanId());
    }
}
