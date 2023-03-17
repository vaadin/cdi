/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import javax.inject.Inject;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("invalid-injection")
public class InvalidView extends Div {

    @Inject
    @RouteScopeOwner(ErrorParentView.class)
    /*
     * There is no a ErrorParentView in navigation: this injection has no scope.
     * Pseudo-scope @RouteScoped is used here with the component to immediately
     * get an exception, for normal scope proxy is created and the exception
     * won't be thrown immediately.
     */
    private ErrorParentView bean;

    public InvalidView() {
        setId("invalid-injection");
        setText("This view should not be shown since the "
                + "injection has no scope and an exception should be thrown");
    }
}
