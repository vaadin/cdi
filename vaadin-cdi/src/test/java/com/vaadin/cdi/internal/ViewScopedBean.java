/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalViewScoped;

@NormalViewScoped
public class ViewScopedBean {

    public static final String ID = "view-scoped-bean";

    public ViewScopedBean() {
    }

    public ViewScopedBean getUnderlyingInstance() {
        return this;
    }

}
