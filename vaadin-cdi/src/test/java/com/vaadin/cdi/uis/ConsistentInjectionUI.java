package com.vaadin.cdi.uis;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI("")
public class ConsistentInjectionUI extends UI {

    private Layout layout = new VerticalLayout();

    public static final String POSTCONSTRUCT_ID = "postconstruct";
    public static final String INIT_ID = "init";

    @Inject
    private MyBean bean;

    @PostConstruct
    private void test() {
        System.out.println("@PostConstruct: " + bean.getBeanId());
        Label postconstuctLabel = new Label(""
                + bean.getBeanId());
        postconstuctLabel.setId(POSTCONSTRUCT_ID);
        layout.addComponent(postconstuctLabel);
    }

    @Override
    protected void init(VaadinRequest request) {
        System.out.println("init(): " + bean.getBeanId());

        Label label = new Label("+ConsistentInjectionUI");
        label.setId("label");
        layout.addComponent(label);

        Label initLabel = new Label("" + bean.getBeanId());
        initLabel.setId(INIT_ID);
        layout.addComponent(initLabel);
        setContent(layout);
    }
}
