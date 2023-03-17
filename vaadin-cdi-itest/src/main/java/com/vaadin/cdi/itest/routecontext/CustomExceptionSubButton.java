/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import java.util.UUID;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.html.NativeButton;

@RouteScoped
@RouteScopeOwner(ErrorHandlerView.class)
public class CustomExceptionSubButton extends AbstractCountedView {

    public CustomExceptionSubButton() {
        NativeButton button = new NativeButton();
        button.setId("custom-exception-button");
        button.setText(UUID.randomUUID().toString());
        add(button);
    }
}
