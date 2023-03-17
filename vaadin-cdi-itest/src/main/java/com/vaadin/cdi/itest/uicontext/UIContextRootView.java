/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.uicontext;

import com.vaadin.cdi.itest.uicontext.UIScopedLabel.SetTextEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Route("")
public class UIContextRootView extends Div {

    public static final String CLOSE_UI_BTN = "CLOSE_UI_BTN";
    public static final String CLOSE_SESSION_BTN = "CLOSE_SESSION_BTN";
    public static final String TRIGGER_EVENT_BTN = "TRIGGER_EVENT_BTN";
    public static final String INJECTER_LINK = "injecter view";
    public static final String UISCOPED_LINK = "uiscoped view";
    public static final String UIID_LABEL = "UIID_LABEL";
    public static final String NORMALSCOPED_LINK = "normalscoped bean view";
    public static final String EVENT_PAYLOAD = "EVENT_PAYLOAD";

    @Inject
    private UIScopedLabel label;

    @Inject
    private Event<SetTextEvent> setTextEventTrigger;

    @PostConstruct
    private void init() {
        final String uiIdStr = UI.getCurrent().getUIId() + "";
        label.setText(uiIdStr);

        final Label uiId = new Label(uiIdStr);
        uiId.setId(UIID_LABEL);

        final NativeButton closeUI = new NativeButton("close UI",
                event -> getUI().ifPresent(UI::close));
        closeUI.setId(CLOSE_UI_BTN);

        final NativeButton closeSession = new NativeButton("close session",
                event -> getUI().ifPresent(ui -> ui.getSession().close()));
        closeSession.setId(CLOSE_SESSION_BTN);

        final NativeButton triggerEvent = new NativeButton("event trigger",
                event -> setTextEventTrigger.fire(new SetTextEvent(EVENT_PAYLOAD)));
        triggerEvent.setId(TRIGGER_EVENT_BTN);

        final Div navDiv = new Div(
                new RouterLink(INJECTER_LINK, UIScopeInjecterView.class),
                new RouterLink(UISCOPED_LINK, UIScopedView.class),
                new RouterLink(NORMALSCOPED_LINK, UINormalScopedBeanView.class)
        );

        add(
                new Div(uiId),
                new Div(closeUI, closeSession),
                new Div(triggerEvent),
                new Div(this.label),
                navDiv);
    }

}
