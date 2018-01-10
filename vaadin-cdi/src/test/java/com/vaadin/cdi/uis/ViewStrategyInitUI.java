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

package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDINavigator;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.ViewContextStrategies;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.inject.Inject;

@CDIUI("")
public class ViewStrategyInitUI extends UI {
    private static final String LABEL_ID = "label";
    public static final String OUTPUT_ID = "output";
    public static final String VIEWNAME_BTN_ID = "viewname";
    public static final String VIEWNAMEPARAMS_BTN_ID = "viewnameparams";

    @Inject
    ViewContextStrategies.ViewName viewName;

    @Inject
    ViewContextStrategies.ViewNameAndParameters viewNameAndParameters;

    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this, view -> { });

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        final Label output = new Label();
        output.setId(OUTPUT_ID);
        layout.addComponent(output);

        final Button viewNameBtn = new Button("viewname", event -> {
            final boolean contains = viewName.inCurrentContext("home", "p2");
            output.setValue(String.valueOf(contains));
        });
        viewNameBtn.setId(VIEWNAME_BTN_ID);
        layout.addComponent(viewNameBtn);

        final Button viewNameParamsBtn = new Button("viewnameparams", event -> {
            final boolean contains = viewNameAndParameters.inCurrentContext("home", "p1");
            output.setValue(String.valueOf(contains));
        });

        viewNameParamsBtn.setId(VIEWNAMEPARAMS_BTN_ID);
        layout.addComponent(viewNameParamsBtn);

        setContent(layout);
    }

    @CDIView(value = "home")
    public static class HomeView implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

}
