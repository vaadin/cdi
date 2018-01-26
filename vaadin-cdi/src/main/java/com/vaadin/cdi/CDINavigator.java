/*
 * Copyright 2000-2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
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

import javax.enterprise.event.Event;
import javax.inject.Inject;

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
