/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

public class UIUnderTestContext implements UnderTestContext {

    private VaadinSession session;
    private UI ui;
    private static int uiIdNdx = 0;
    private static SessionUnderTestContext sessionContextUnderTest;

    public UIUnderTestContext() {
        this(null);
    }

    protected UIUnderTestContext(VaadinSession session) {
        this.session = session;
    }

    private void mockUI() {
        if (session == null) {
            mockSession();
        }

        ui = new UI();
        ui.getInternals().setSession(session);
        uiIdNdx++;
        ui.doInit(null, uiIdNdx);
    }

    private void mockSession() {
        if (sessionContextUnderTest == null) {
            sessionContextUnderTest = new SessionUnderTestContext();
            sessionContextUnderTest.activate();
        }
        session = sessionContextUnderTest.getSession();
    }

    @Override
    public void activate() {
        if (ui == null) {
            mockUI();
        }
        UI.setCurrent(ui);
    }

    @Override
    public void tearDownAll() {
        UI.setCurrent(null);
        uiIdNdx = 0;
        if (sessionContextUnderTest != null) {
            sessionContextUnderTest.tearDownAll();
            sessionContextUnderTest = null;
        }
    }

    @Override
    public void destroy() {
        ComponentUtil.onComponentDetach(ui);
    }

    public UI getUi() {
        return ui;
    }

}
