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
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

@Alternative
@CDIUI("PlainUI")
public class PlainColidingAlternativeUI extends UI {
    public static final String CONSTRUCT_COUNT = "PlainColidingAlternativeUIConstruct";
    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+PlainAlternativeUI");
        label.setId("label");
        layout.addComponent(label);
        setContent(layout);
    }

}
