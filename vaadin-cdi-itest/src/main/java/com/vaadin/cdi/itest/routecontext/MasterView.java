/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

@RouteScoped
@Route("")
@RoutePrefix("master")
public class MasterView extends AbstractCountedView
        implements RouterLayout, AfterNavigationObserver {

    public static final String ASSIGNED = "assigned";
    public static final String ASSIGNED_BEAN_LABEL = "ASSIGNED";
    public static final String APART = "apart";
    public static final String APART_BEAN_LABEL = "APART";

    @Inject
    @RouteScopeOwner(MasterView.class)
    private AssignedBean assignedBean;
    private Label assignedLabel;

    @PostConstruct
    private void init() {
        assignedLabel = new Label();
        assignedLabel.setId(ASSIGNED_BEAN_LABEL);
        add(new Label("MASTER"), new Div(assignedLabel),
                new Div(new RouterLink(ASSIGNED, DetailAssignedView.class)),
                new Div(new RouterLink(APART, DetailApartView.class)));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        assignedLabel.setText(assignedBean.getData());
    }

}
