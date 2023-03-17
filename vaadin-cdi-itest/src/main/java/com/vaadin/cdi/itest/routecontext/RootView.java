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

import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "", layout = MainLayout.class)
@RouteScoped
public class RootView extends AbstractCountedView {

    public static final String MASTER = "master";
    public static final String REROUTE = "reroute";
    public static final String POSTPONE = "postpone";
    public static final String EVENT = "event";
    public static final String ERROR = "ERROR";

    @PostConstruct
    private void init() {
        add(new Div(new Label("ROOT")),
                new Div(new RouterLink(MASTER, MasterView.class)),
                new Div(new RouterLink(REROUTE, RerouteView.class)),
                new Div(new RouterLink(POSTPONE, PostponeView.class)),
                new Div(new RouterLink(EVENT, EventView.class)),
                new Div(new RouterLink(ERROR, ErrorView.class)));
    }

}
