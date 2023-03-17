/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.uievents;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.EventObject;
import java.util.List;

@Route("uievents")
@UIScoped
public class UIEventsView extends Div implements AfterNavigationObserver {

    public static final String POLL_FROM_CLIENT = "POLL_FROM_CLIENT";
    public static final String NAVIGATION_EVENTS = "NAVIGATION_EVENTS";

    @Inject
    private NavigationObserver navigationObserver;

    @PostConstruct
    private void init() {
        UI.getCurrent().setPollInterval(500);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        showNavigationEvents();
    }

    private void showNavigationEvents() {
        Div events = new Div();
        events.setId(NAVIGATION_EVENTS);
        List<EventObject> navigationEvents = navigationObserver.getNavigationEvents();
        navigationEvents.stream()
                .map(event -> new Label(event.getClass().getSimpleName()))
                .forEach(events::add);
        add(events);
    }

    private void showPollEvent(@Observes PollEvent pollEvent) {
        final Label poll = new Label(pollEvent.isFromClient() + "");
        poll.setId(POLL_FROM_CLIENT);
        add(new Div(poll));
    }
}
