/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.shiro;

import com.vaadin.cdi.views.AbstractNavigatableView;
import com.vaadin.ui.Component;

public abstract class AbstractShiroTestView extends AbstractNavigatableView {

    protected static final String LABEL_ID = "label";

    @Override
    protected Component buildNavigationBar() {
        NavigationButton guestButton = new NavigationButton("Guest",
                GuestView.VIEW_ID, GuestView.VIEW_ID);
        NavigationButton viewerButton = new NavigationButton("Viewer",
                ViewerView.VIEW_ID, ViewerView.VIEW_ID);
        NavigationButton adminButton = new NavigationButton("Admin",
                AdminView.VIEW_ID, AdminView.VIEW_ID);

        return buildNavigationBar(guestButton, viewerButton, adminButton);
    }

}