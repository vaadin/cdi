package com.vaadin.cdi.uis;


import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.VaadinSessionScoped;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@CDIUI
public class SessionUI extends UI {

    public static final String SETVALUEBTN_ID = "setvalbtn";
    public static final String VALUELABEL_ID = "label";
    public static final String VALUE = "session";
    public static final String INVALIDATEBTN_ID = "invalidatebtn";

    @Inject
    SessionScopedBean sessionScopedBean;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setSizeFull();

        Button setBtn = new Button("set");
        setBtn.addClickListener(event -> sessionScopedBean.setValue(VALUE));
        setBtn.setId(SETVALUEBTN_ID);
        layout.addComponent(setBtn);

        Button invalidateBtn = new Button("invalidate");
        invalidateBtn.addClickListener(event -> VaadinSession.getCurrent().close());
        invalidateBtn.setId(INVALIDATEBTN_ID);
        layout.addComponent(invalidateBtn);

        Label label = new Label();
        label.setValue(sessionScopedBean.getValue());
        label.setId(VALUELABEL_ID);
        layout.addComponent(label);
    }

    @VaadinSessionScoped
    //Like other vaadin scopes,
    // Serializable is mandatory only if you want a working session serialization
    public static class SessionScopedBean {
        public static final String DESTROY_COUNT = "SessionScopedBeanDestroy";

        @Inject
        Counter counter;

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @PreDestroy
        private void preDestroy() {
            counter.increment(DESTROY_COUNT);
        }
    }
}
