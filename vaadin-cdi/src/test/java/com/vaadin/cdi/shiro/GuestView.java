package com.vaadin.cdi.shiro;

import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(GuestView.VIEW_ID)
public class GuestView extends AbstractShiroTestView {

    public static final String VIEW_ID = "";

    @Override
    protected Component buildContent() {
        Label label = new Label("Guest view");
        label.setId(LABEL_ID);
        
        VerticalLayout layout = new VerticalLayout(label, new LoginPane());
        layout.setSizeFull();
        return layout;
    }

}
