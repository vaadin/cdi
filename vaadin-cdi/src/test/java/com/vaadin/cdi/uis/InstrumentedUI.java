package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinUI
@VaadinUIScoped
public class InstrumentedUI extends UI {

    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    private int clickCount;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
        this.clickCount = 0;

    }

    @Override
    protected void init(WrappedRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        final Label label = new Label("+InstrumentedUI");
        label.setId("label");
        Button button = new Button("button", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                label.setValue(String.valueOf(++clickCount));
            }
        });
        button.setId("button");
        layout.addComponent(label);
        layout.addComponent(button);
        setContent(layout);
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }

}
