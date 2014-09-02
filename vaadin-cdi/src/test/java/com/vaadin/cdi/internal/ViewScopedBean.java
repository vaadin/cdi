package com.vaadin.cdi.internal;

import com.vaadin.cdi.ViewScoped;

@ViewScoped
public class ViewScopedBean {

    public static final String ID = "view-scoped-bean";

    public ViewScopedBean() {
    }

    public ViewScopedBean getUnderlyingInstance() {
        return this;
    }

}
