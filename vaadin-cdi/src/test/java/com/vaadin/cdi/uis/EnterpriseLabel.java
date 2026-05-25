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
package com.vaadin.cdi.uis;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Label;

@UIScoped
public class EnterpriseLabel extends Label {
    public static final String ENTERPRISE_LABEL = "enterpriseLabel";

    @Inject
    Boundary boundary;

    @PostConstruct
    public void init() {
        setId(ENTERPRISE_LABEL);
        setValue(boundary.echo("testing"));
    }
}
