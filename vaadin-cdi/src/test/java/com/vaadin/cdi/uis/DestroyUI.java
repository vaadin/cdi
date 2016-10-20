package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
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
import java.util.concurrent.atomic.AtomicInteger;

@CDIUI("")
public class DestroyUI extends UI {
    private final static AtomicInteger COUNTER = new AtomicInteger(0);
    public static final String CLOSE_BTN_ID = "close";
    public static final String NAVIGATE_BTN_ID = "navigate";
    public static final String LABEL_ID = "label";

    @Inject
    CDIViewProvider viewProvider;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    @PreDestroy
    public void destroy() {
        COUNTER.decrementAndGet();
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("open");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        Button closeBtn = new Button("close UI");
        closeBtn.setId(CLOSE_BTN_ID);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
                label.setValue(CLOSE_BTN_ID);
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
                        label.setValue(NAVIGATE_BTN_ID);
                    }
                });
                navigator.addProvider(viewProvider);
                navigator.navigateTo("scopedInstrumentedView");
            }
        });
        layout.addComponent(viewNavigateBtn);

        setContent(layout);

    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }
}
