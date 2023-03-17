/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.uievents;

import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;

import javax.enterprise.event.Observes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

@VaadinSessionScoped
public class NavigationObserver implements Serializable {

    private List<EventObject> navigationEvents = new ArrayList<>();

    private void onBeforeLeave(@Observes BeforeLeaveEvent event) {
        navigationEvents.add(event);
    }

    private void onBeforeEnter(@Observes BeforeEnterEvent event) {
        navigationEvents.add(event);
    }

    private void onAfterNavigation(@Observes AfterNavigationEvent event) {
        navigationEvents.add(event);
    }

    public List<EventObject> getNavigationEvents() {
        return navigationEvents;
    }

}
