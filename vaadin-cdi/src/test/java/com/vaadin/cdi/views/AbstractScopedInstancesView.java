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
package com.vaadin.cdi.views;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

public abstract class AbstractScopedInstancesView extends AbstractNavigatableView implements
        View {

    public static final String UISCOPED_VIEW = "uiscoped";
    public static final String VIEWSCOPED_VIEW = "";

    public static final String NAVIGATE_TO_UISCOPED = "navigate-uiscoped";
    public static final String NAVIGATE_TO_VIEWSCOPED = "navigate-viewscoped";

    @Override
    protected Component buildNavigationBar() {
        NavigationButton viewScopedButton = new NavigationButton("ViewScoped",
                VIEWSCOPED_VIEW, NAVIGATE_TO_VIEWSCOPED);
        NavigationButton uiScopedButton = new NavigationButton("UIScoped",
                UISCOPED_VIEW, NAVIGATE_TO_UISCOPED);

        return buildNavigationBar(viewScopedButton, uiScopedButton);
    }

}
