/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.PostConstruct;

@Route("postpone")
@RouteScoped
public class PostponeView extends AbstractCountedView implements BeforeLeaveObserver {

    public static final String NAVIGATE = "NAVIGATE";
    public static final String POSTPONED_ROOT = "postpone";

    private BeforeLeaveEvent.ContinueNavigationAction navigationAction;

    @PostConstruct
    private void init() {
        NativeButton navBtn = new NativeButton("navigate", clickEvent
                -> navigationAction.proceed());
        navBtn.setId(NAVIGATE);

        add(
                new Div(new RouterLink(POSTPONED_ROOT, RootView.class)),
                new Div(navBtn)
        );
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        navigationAction = beforeLeaveEvent.postpone();
    }

}
