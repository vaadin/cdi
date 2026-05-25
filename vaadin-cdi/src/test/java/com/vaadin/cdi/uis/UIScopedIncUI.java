/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.internal.ClusterIncTestLayout;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import jakarta.inject.Inject;

@CDIUI("uiscoped")
public class UIScopedIncUI extends UI {

    @Inject
    UIScopedBean uiScopedBean;

    @Inject
    NormalUIScopedBean normalUIScopedBean;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        ClusterIncTestLayout layout = new ClusterIncTestLayout();
        setContent(layout);
        layout.setSizeFull();

        layout.init(uiScopedBean, normalUIScopedBean);
    }

    @UIScoped
    public static class UIScopedBean extends ClusterIncTestLayout.IncTestBean {
    }

    @NormalUIScoped
    public static class NormalUIScopedBean extends ClusterIncTestLayout.IncTestBean {
    }

}
