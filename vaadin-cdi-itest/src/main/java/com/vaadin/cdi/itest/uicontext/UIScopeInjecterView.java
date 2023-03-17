/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.uicontext;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Route("injecter")
public class UIScopeInjecterView extends Div {
    @Inject
    private UIScopedLabel label;

    @PostConstruct
    private void init() {
        add(label);
    }
}
