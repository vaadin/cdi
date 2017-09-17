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

import com.vaadin.cdi.*;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategyQualifier;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import javax.inject.Inject;
import java.lang.annotation.*;
import java.util.Objects;

@CDIUI("")
public class ViewStrategyCallUI extends UI {
    public static final String VIEWNAME_OUTPUT_ID = "viewnameid";
    public static final String PARAMETERS_OUTPUT_ID = "parametersid";
    public static final String BEANVALUE_OUTPUT_ID = "beanvalueid";
    public static final String TARGETSTATE_ID = "targetstate";
    public static final String NAVBTN_ID = "navbtn";
    public static final String BEANVALUE = "beanvalue";
    public static final String UNDEFINED = "undefined";
    public static final String SAMEVIEW = "same";
    public static final String OTHERVIEW = "other";

    @Inject
    CDINavigator navigator;
    private Label viewName;
    private Label parameters;
    private Label beanValue;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this, view -> { });

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setContent(layout);

        final Label label = new Label("label");
        label.setId("label");
        layout.addComponent(label);

        viewName = new Label();
        viewName.setId(VIEWNAME_OUTPUT_ID);
        viewName.setValue(UNDEFINED);
        layout.addComponent(viewName);

        parameters = new Label();
        parameters.setId(PARAMETERS_OUTPUT_ID);
        parameters.setValue(UNDEFINED);
        layout.addComponent(parameters);

        beanValue = new Label();
        beanValue.setId(BEANVALUE_OUTPUT_ID);
        beanValue.setValue(UNDEFINED);
        layout.addComponent(beanValue);

        final TextField targetState = new TextField();
        targetState.setId(TARGETSTATE_ID);
        layout.addComponent(targetState);

        final Button navBtn = new Button("navigate",
                event -> navigator.navigateTo(targetState.getValue()));
        navBtn.setId(NAVBTN_ID);
        layout.addComponent(navBtn);
    }

    @CDIView(value = SAMEVIEW)
    public static class SameView implements View {
        @Inject
        ViewScopedBean bean;

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            final ViewStrategyCallUI ui = (ViewStrategyCallUI) UI.getCurrent();
            ui.beanValue.setValue(bean.getValue());
        }
    }

    @CDIView(value = OTHERVIEW)
    public static class OtherView implements View {
        @Inject
        ViewScopedBean bean;

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            final ViewStrategyCallUI ui = (ViewStrategyCallUI) UI.getCurrent();
            ui.beanValue.setValue(bean.getValue());
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @Inherited
    @ViewContextStrategyQualifier
    public @interface TestDriven {
    }

    @CDIView(value = "")
    @TestDriven
    public static class RootView implements View {
        @Inject
        ViewScopedBean bean;
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.setValue(BEANVALUE);
        }
    }

    @TestDriven
    public static class TestStrategy implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
            final ViewStrategyCallUI ui = (ViewStrategyCallUI) UI.getCurrent();
            ui.viewName.setValue(viewName);
            ui.parameters.setValue(parameters);
            return !Objects.equals(viewName, OTHERVIEW);
        }
    }

    @NormalViewScoped
    public static class ViewScopedBean {
        private String value = UNDEFINED;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
