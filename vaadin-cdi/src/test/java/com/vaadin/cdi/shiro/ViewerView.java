package com.vaadin.cdi.shiro;

import javax.annotation.security.RolesAllowed;

import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@CDIView(ViewerView.VIEW_ID)
@RolesAllowed("viewer")
public class ViewerView extends AbstractShiroTestView {

    public static final String VIEW_ID = "viewer";

    @Override
    protected Component buildContent() {
        Label label = new Label(VIEW_ID);
        label.setId(LABEL_ID);
        return label;
    }

}
