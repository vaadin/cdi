/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@RouteScoped
@RouteScopeOwner(MasterView.class)
@Route(value = "assigned", layout = MasterView.class)
public class DetailAssignedView extends AbstractCountedView implements AfterNavigationObserver {

    public static final String MASTER = "master";
    public static final String BEAN_LABEL = "BEAN_LABEL";

    @Inject
    @RouteScopeOwner(MasterView.class)
    private AssignedBean assignedBean;
    private Label assignedLabel;

    @PostConstruct
    private void init() {
        assignedLabel = new Label();
        assignedLabel.setId(BEAN_LABEL);
        assignedBean.setData("ASSIGNED");
        add(
                assignedLabel,
                new Div(new RouterLink(MASTER, MasterView.class))
        );
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        assignedLabel.setText(assignedBean.getData());
    }

}

