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

@RouteScoped
@RouteScopeOwner(ErrorHandlerView.class)
public class CustomExceptionSubDiv extends AbstractCountedView {

    public CustomExceptionSubDiv() {
        setId("custom-exception-div");
        setText(UUID.randomUUID().toString());
    }
}
