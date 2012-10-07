package com.vaadin.cdi.uis;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

@VaadinUI
public class UIWithCDIDependentListener extends UI {

    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @Inject
    private javax.enterprise.event.Event<String> events;

    @Inject
    private DependentCDIEventListener toBeDependent;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+UIWithCDIDependentListener");
        label.setId("label");
        Button button = new Button("button", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                      events.fire("Fired: " + (System.currentTimeMillis()));
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
