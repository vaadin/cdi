/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends Div implements RouterLayout {

    public static final String UIID = "UIID";

    public static final String PRESERVE = "preserve";
    public static final String INVALID = "invalid";
    public static final String PARENT_NO_OWNER = "parent-no-owner";
    public static final String CHILD_NO_OWNER = "child-no-owner";

    private Label uiIdLabel;

    public MainLayout() {
        add(new RouterLink(PRESERVE, PreserveOnRefreshView.class),
                new RouterLink(INVALID, InvalidView.class),
                new RouterLink(PARENT_NO_OWNER, ParentNoOwnerView.class),
                new RouterLink(CHILD_NO_OWNER, ChildNoOwnerView.class));
        ;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (uiIdLabel != null) {
            remove(uiIdLabel);
        }
        uiIdLabel = new Label(attachEvent.getUI().getUIId() + "");
        uiIdLabel.setId(UIID);
        add(uiIdLabel);
    }
}
