/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.uicontext;

import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Route("normalscopedbean")
public class UINormalScopedBeanView extends Div {

    public static final String UIID_LABEL = "UIID_LABEL";

    @Inject
    private SessionScopedUIidService sessionScopedUIidService;

    @PostConstruct
    private void init() {
        final Label label = new Label(sessionScopedUIidService.getUiIdStr());
        label.setId(UIID_LABEL);
        add(label);
    }

    @NormalUIScoped
    public static class NormalUIScopedUIidService {
        private String uiIdStr;

        @PostConstruct
        public void init() {
            uiIdStr = UI.getCurrent().getUIId() + "";
        }

        public String getUiIdStr() {
            return uiIdStr;
        }
    }

    @VaadinSessionScoped
    public static class SessionScopedUIidService {
        @Inject
        private NormalUIScopedUIidService normalUIScopedUIidService;

        public String getUiIdStr() {
            return normalUIScopedUIidService.getUiIdStr();
        }
    }
}
