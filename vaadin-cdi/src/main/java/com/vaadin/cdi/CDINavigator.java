/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import com.vaadin.cdi.internal.ViewContextualStorageManager;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * Vaadin Navigator as a CDI Bean.
 *
 * Have to be initialized once with an "init(...)" method.
 * During initialization a {@link CDIViewProvider} added automatically.
 *
 * This class is responsible for controlling {@link com.vaadin.cdi.ViewScoped} context,
 * so initialization is mandatory for view scope.
 */
@NormalUIScoped
public class CDINavigator extends Navigator {

    @Inject
    private ViewContextualStorageManager viewContextualStorageManager;

    @Inject
    private CDIViewProvider cdiViewProvider;

    @Inject
    @AfterViewChange
    private Event<ViewChangeEvent> afterViewChangeTrigger;

    /**
     * {@inheritDoc}
     *
     * During initialization a {@link CDIViewProvider} added automatically.
     */
    @Override
    public void init(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
        super.init(ui, stateManager, display);
        addProvider(cdiViewProvider);
    }

    /**
     * Init method like {@link Navigator#Navigator(UI, ViewDisplay)}.
     *
     * During initialization a {@link CDIViewProvider} added automatically.
     */
    public void init(UI ui, ViewDisplay display) {
        init(ui, null, display);
    }

    /**
     * Init method like {@link Navigator#Navigator(UI, SingleComponentContainer)}.
     *
     * During initialization a {@link CDIViewProvider} added automatically.
     */
    public void init(UI ui, SingleComponentContainer container) {
        init(ui, new SingleComponentContainerViewDisplay(container));
    }

    /**
     * Init method like {@link Navigator#Navigator(UI, ComponentContainer)}.
     *
     * During initialization a {@link CDIViewProvider} added automatically.
     */
    public void init(UI ui, ComponentContainer container) {
        init(ui, new ComponentContainerViewDisplay(container));
    }

    @Override
    protected boolean fireBeforeViewChange(ViewChangeEvent event) {
        final boolean navigationAllowed = super.fireBeforeViewChange(event);
        if (navigationAllowed) {
            viewContextualStorageManager.applyChange(event);
        } else {
            viewContextualStorageManager.revertChange(event);
        }
        return navigationAllowed;
    }

    @Override
    protected void fireAfterViewChange(ViewChangeEvent event) {
        super.fireAfterViewChange(event);
        afterViewChangeTrigger.fire(event);
    }
}
