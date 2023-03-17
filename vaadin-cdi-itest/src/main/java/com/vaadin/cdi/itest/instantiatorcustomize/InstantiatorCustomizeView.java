/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.instantiatorcustomize;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;

@Route("")
public class InstantiatorCustomizeView extends Div {
    public static final String VIEW = "VIEW";
    public static final String CUSTOMIZED = "CUSTOMIZED";

    @PostConstruct
    private void init() {
        setId(VIEW);
    }

    public void customize() {
        setText(CUSTOMIZED);
    }
}
