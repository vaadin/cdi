package com.vaadin.cdi.uis;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.views.BeanView;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("reindeer")
@CDIUI("")
public class InjectionUI extends UI {

    public static final String beanId1 = "bean1";
    public static final String beanId2 = "bean2";

    @Inject
    BeanView myBeanView1;

    @Inject
    BeanView myBeanView2;

    public InjectionUI() {
        System.out.println("UI constructor");
    }

    @PostConstruct
    private void test() {
        System.out.println("Post construct current: " + UI.getCurrent());
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Label label = new Label("InjectionUI");
        label.setId("label");

        layout.addComponent(label);
        layout.addComponent(myBeanView1);
        layout.addComponent(myBeanView2);

        myBeanView1.setId(beanId1);
        myBeanView2.setId(beanId2);

        System.out.println("UI init");
    }

}