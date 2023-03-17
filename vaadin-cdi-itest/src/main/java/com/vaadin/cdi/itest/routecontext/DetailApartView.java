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
@Route(value = "apart", layout = MasterView.class)
public class DetailApartView extends AbstractCountedView implements AfterNavigationObserver {

    public static final String MASTER = "master";
    public static final String BEAN_LABEL = "BEAN_LABEL";

    @Inject
    @RouteScopeOwner(DetailApartView.class)
    private ApartBean apartBean;
    private Label apartLabel;

    @PostConstruct
    private void init() {
        apartLabel = new Label();
        apartLabel.setId(BEAN_LABEL);
        apartBean.setData("APART");
        add(
                apartLabel,
                new Div(new RouterLink(MASTER, MasterView.class))
        );
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        apartLabel.setText(apartBean.getData());
    }

}
