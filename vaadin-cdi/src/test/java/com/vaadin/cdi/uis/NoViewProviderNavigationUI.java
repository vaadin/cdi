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

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIUI(value = "noViewProviderNavigationUI")
public class NoViewProviderNavigationUI extends UI {

    public static final String CONSTRUCT_COUNT = "NoViewProviderNavigationUIConstruct";
    public static final String NAVIGATION_COUNT = "NoViewProviderNavigationUINavigation";
    @Inject
    InstrumentedView view;
    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
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
                counter.increment(NAVIGATION_COUNT);
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

}
