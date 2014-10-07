package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;

@NormalUIScoped
public class UIScopedBean {

    public static final String ID = "ui-scoped-bean";

    public UIScopedBean() {
    }

    public UIScopedBean getUnderlyingInstance() {
        return this;
    }

}
