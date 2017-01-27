package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@CDIUI("")
public class DestroyUI extends UI {
    public static final String CLOSE_BTN_ID = "close";
    public static final String NAVIGATE_BTN_ID = "navigate";
    public static final String LABEL_ID = "label";
    public static final String UIID_ID = "UIID";
    public static final String DESTROY_COUNT = "uidestroycount";

    @Inject
    CDIViewProvider viewProvider;

    @Inject
    Counter counter;

    @PreDestroy
    public void destroy() {
        counter.increment(DESTROY_COUNT + getUIId());
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        final Label uiId = new Label(String.valueOf(getUIId()));
        uiId.setId(UIID_ID);
        layout.addComponent(uiId);

        Button closeBtn = new Button("close UI");
        closeBtn.setId(CLOSE_BTN_ID);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        layout.addComponent(closeBtn);

        Button viewNavigateBtn = new Button("navigate");
        viewNavigateBtn.setId(NAVIGATE_BTN_ID);
        viewNavigateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final Navigator navigator = new Navigator(DestroyUI.this, new ViewDisplay() {
                    @Override
                    public void showView(View view) {
                    }
                });
                navigator.addProvider(viewProvider);
                navigator.navigateTo("test");
            }
        });
        layout.addComponent(viewNavigateBtn);

        setContent(layout);
    }

}
