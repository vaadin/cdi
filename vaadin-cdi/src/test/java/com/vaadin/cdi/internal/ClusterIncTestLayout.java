package com.vaadin.cdi.internal;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class ClusterIncTestLayout extends VerticalLayout {

    public static final String VALUE_LABEL_ID = "value";
    public static final String NORMALVALUE_LABEL_ID = "normalvalue";
    public static final String INC_BUTTON_ID = "incButton";
    public static final String PORT_LABEL_ID = "port";

    private IncTestBean incTestBean;
    private IncTestBean incNormalTestBean;

    public ClusterIncTestLayout() {
        setSizeFull();

        final Label valueLabel = new Label();
        valueLabel.setId(VALUE_LABEL_ID);
        addComponent(valueLabel);

        final Label normalValueLabel = new Label();
        normalValueLabel.setId(NORMALVALUE_LABEL_ID);
        addComponent(normalValueLabel);

        final Label portLabel = new Label();
        portLabel.setId(PORT_LABEL_ID);
        addComponent(portLabel);

        Button incBtn = new Button("increment");
        incBtn.setId(INC_BUTTON_ID);
        incBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                valueLabel.setValue(String.valueOf(incTestBean.incrementAndGet()));
                normalValueLabel.setValue(String.valueOf(incNormalTestBean.incrementAndGet()));
                HttpServletRequest servletRequest = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                        .getHttpServletRequest();
                portLabel.setValue(String.valueOf(servletRequest.getLocalPort()));
            }
        });
        addComponent(incBtn);
    }

    public void init(IncTestBean incTestBean, IncTestBean incNormalTestBean) {
        this.incTestBean = incTestBean;
        this.incNormalTestBean = incNormalTestBean;
    }

    public static class IncTestBean implements Serializable {
        int counter = 0;

        public int incrementAndGet() {
            return ++counter;
        }
    }

}
