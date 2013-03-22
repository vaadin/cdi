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
 */

package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI(value = "noViewProviderNavigationUI")
public class NoViewProviderNavigationUI extends UI {

    @Inject
    InstrumentedView view;
    private final static AtomicInteger COUNTER = new AtomicInteger(0);
    private final static AtomicInteger NAVIGATION_COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final HorizontalLayout horizontalLayout = new HorizontalLayout();

        final Label label = new Label("+NoViewProviderNavigationUI");
        label.setId("label");

        Button navigate = new Button("Navigate", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                NAVIGATION_COUNTER.incrementAndGet();
                Navigator navigator = new Navigator(
                        NoViewProviderNavigationUI.this, horizontalLayout);
                navigator.addView("instrumentedView", view);
                navigator.navigateTo("instrumentedView");
            }
        });
        navigate.setId("navigate");
        verticalLayout.addComponent(label);
        verticalLayout.addComponent(navigate);
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static int getNumberOfNavigations() {
        return NAVIGATION_COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
        NAVIGATION_COUNTER.set(0);
    }

}
