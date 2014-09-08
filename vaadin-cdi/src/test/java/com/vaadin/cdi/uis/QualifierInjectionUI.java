package com.vaadin.cdi.uis;

import javax.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.Alpha;
import com.vaadin.cdi.internal.Beta;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI
public class QualifierInjectionUI extends UI {

    public static final String DEFAULT_ID = "default";
    public static final String ALPHA_ID = "alpha";
    public static final String BETA_ID = "beta";

    @Inject
    private MyBean defaultBean;

    @Inject
    @Alpha
    private MyBean alphaBean;

    @Inject
    @Beta
    private MyBean betaBean;

    @Override
    protected void init(VaadinRequest request) {
        Layout layout = new VerticalLayout();
        layout.setSizeFull();

        Label label = new Label("+QualifierInjectionUI");
        label.setId("label");
        layout.addComponent(label);

        Label defaultLabel = new Label(defaultBean.getClass().getSimpleName());
        defaultLabel.setId(DEFAULT_ID);
        layout.addComponent(defaultLabel);

        Label alphaLabel = new Label(alphaBean.getClass().getSimpleName());
        alphaLabel.setId(ALPHA_ID);
        layout.addComponent(alphaLabel);

        Label betaLabel = new Label(betaBean.getClass().getSimpleName());
        betaLabel.setId(BETA_ID);
        layout.addComponent(betaLabel);

        setContent(layout);
    }

}
