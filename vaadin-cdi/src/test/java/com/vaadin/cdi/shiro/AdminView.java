package com.vaadin.cdi.shiro;

import javax.annotation.security.RolesAllowed;

import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@CDIView(AdminView.VIEW_ID)
@RolesAllowed("admin")
public class AdminView extends AbstractShiroTestView {

    public static final String VIEW_ID = "admin";

    @Override
    protected Component buildContent() {
        Label label = new Label(VIEW_ID);
        label.setId(LABEL_ID);
        return label;
    }

}
