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
import com.vaadin.cdi.internal.Counter;
import com.vaadin.cdi.viewcontextstrategy.EveryNavigationDriven;
import com.vaadin.cdi.viewcontextstrategy.ViewNameAndParametersDriven;
import com.vaadin.cdi.viewcontextstrategy.ViewNameDriven;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

@CDIUI("")
public class ViewStrategyUI extends UI {
    public static final String BYVIEWNAME = "byviewname";
    public static final String BYVIEWNAMEPARAMS = "byviewnameparams";
    public static final String BYALWAYS = "byalways";
    public static final String OTHER = "other";

    public static final String TARGETSTATE_ID = "targetstate";
    public static final String NAVBTN_ID = "navbtn";

    public static final String VALUE_LABEL_ID = "valuelabel";
    private static final String LABEL_ID = "label";

    @Inject
    CDINavigator navigator;
    @Inject
    ViewScopedBean bean;
    private Label value;


    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        value = new Label();
        value.setId(VALUE_LABEL_ID);
        layout.addComponent(value);

        final Panel viewDisplayPanel = new Panel();
        viewDisplayPanel.setContent(new Label());
        layout.addComponent(viewDisplayPanel);

        navigator.init(this, view -> {
        });

        final TextField targetState = new TextField();
        targetState.setId(TARGETSTATE_ID);
        layout.addComponent(targetState);

        final Button navBtn = new Button("navigate",
                event -> {
                    navigator.navigateTo(targetState.getValue());
                    value.setValue(bean.getValue());
                });
        navBtn.setId(NAVBTN_ID);
        layout.addComponent(navBtn);

        setContent(layout);
    }

    @CDIView("")
    public static class DefaultView implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

    @CDIView(value = BYVIEWNAME)
    @ViewNameDriven
    public static class ByViewNameView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "viewnameconstructcount";
        public static String DESTROY_COUNT = "viewnamedestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.enter(event);
        }
    }

    @CDIView(value = BYVIEWNAMEPARAMS)
    @ViewNameAndParametersDriven
    public static class ByViewNameAndParametersView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "viewnameparamsconstructcount";
        public static String DESTROY_COUNT = "viewnameparamsdestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.enter(event);
        }
    }

    @CDIView(value = BYALWAYS)
    @EveryNavigationDriven
    public static class ByAlwaysView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "alwaysconstructcount";
        public static String DESTROY_COUNT = "alwaysdestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.enter(event);
        }
    }

    @CDIView(value = OTHER)
    public static class OtherView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "otherconstructcount";
        public static String DESTROY_COUNT = "otherdestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.enter(event);
        }
    }


    @NormalViewScoped
    public static class ViewScopedBean {
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "beanconstructcount";
        public static String DESTROY_COUNT = "beandestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        private String value = "";

        public String getValue() {
            return value;
        }

        public void enter(ViewChangeListener.ViewChangeEvent event) {
            value = value + "," + event.getViewName() + ":" + event.getParameters();
        }
    }


}
