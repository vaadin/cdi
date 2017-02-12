package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@CDIUI
public class UIScopedCounterUI extends UI {

    public static final String VALUE_LABEL_ID = "value";
    public static final String NORMALVALUE_LABEL_ID = "normalvalue";
    public static final String INC_BUTTON_ID = "incButton";
    public static final String PORT_LABEL_ID = "port";

    @Inject
    UIScopedBean uiScopedBean;

    @Inject
    NormalUIScopedBean normalUIScopedBean;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setSizeFull();

        final Label valueLabel = new Label();
        valueLabel.setId(VALUE_LABEL_ID);
        layout.addComponent(valueLabel);

        final Label normalValueLabel = new Label();
        normalValueLabel.setId(NORMALVALUE_LABEL_ID);
        layout.addComponent(normalValueLabel);

        final Label portLabel = new Label();
        portLabel.setId(PORT_LABEL_ID);
        layout.addComponent(portLabel);

        Button incBtn = new Button("increment");
        incBtn.setId(INC_BUTTON_ID);
        incBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                valueLabel.setValue(String.valueOf(uiScopedBean.incrementAndGet()));
                normalValueLabel.setValue(String.valueOf(normalUIScopedBean.incrementAndGet()));
                HttpServletRequest servletRequest = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                        .getHttpServletRequest();
                portLabel.setValue(String.valueOf(servletRequest.getLocalPort()));
            }
        });
        layout.addComponent(incBtn);

    }

    @UIScoped
    public static class UIScopedBean implements Serializable {
        int counter = 0;

        public int incrementAndGet() {
            return ++counter;
        }
    }

    @NormalUIScoped
    public static class NormalUIScopedBean implements Serializable {
        int counter = 0;

        public int incrementAndGet() {
            return ++counter;
        }
    }

}
