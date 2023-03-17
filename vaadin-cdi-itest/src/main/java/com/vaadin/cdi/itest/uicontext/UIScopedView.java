/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.uicontext;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.PostConstruct;

@Route("uiscoped")
@UIScoped
public class UIScopedView extends Div {

    public static final String VIEWSTATE_LABEL = "VIEWSTATE_LABEL";
    public static final String SETSTATE_BTN = "SETSTATE_BTN";
    public static final String ROOT_LINK = "root view";
    public static final String UISCOPED_STATE = "UISCOPED_STATE";

    @PostConstruct
    private void init() {
        final Label state = new Label("");
        state.setId(VIEWSTATE_LABEL);

        final NativeButton button =
                new NativeButton("set state", event -> state.setText(UISCOPED_STATE));
        button.setId(SETSTATE_BTN);

        add(button, state,
                new RouterLink(ROOT_LINK, UIContextRootView.class));
    }
}
