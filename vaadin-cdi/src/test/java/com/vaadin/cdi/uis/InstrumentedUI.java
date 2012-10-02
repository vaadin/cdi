package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinUI
public class InstrumentedUI extends UI {

    @Inject
    InstrumentedView view;

    @Inject
    CDIViewProvider viewProvider;

    private Navigator navigator;

    private final static AtomicInteger COUNTER = new AtomicInteger(0);
    private int clickCount;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
        this.clickCount = 0;

    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        navigator = new Navigator(this, layout);
        navigator.addProvider(viewProvider);

        final Label label = new Label("+InstrumentedUI");
        label.setId("label");
        Button button = new Button("button", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                label.setValue(String.valueOf(++clickCount));
            }
        });
        button.setId("button");
        Button navigate = new Button("button", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.navigate();
            }
        });
        navigate.setId("navigate");
        layout.addComponent(label);
        layout.addComponent(button);
        layout.addComponent(navigate);
        setContent(layout);
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }

}
