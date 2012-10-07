package com.vaadin.cdi.uis;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

@VaadinUI
public class UIWithCDISelfListener extends UI {

    private final static AtomicInteger COUNTER = new AtomicInteger(0);
    private final static AtomicInteger EVENT_COUNTER = new AtomicInteger(0);

    @Inject
    private javax.enterprise.event.Event<String> events;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+UIWithCDISelfListener");
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

    public void onEventArrival(@Observes String message){
        this.EVENT_COUNTER.incrementAndGet();
        System.out.println("Message arrived!");
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static int getNumberOfDeliveredEvents() {
        return EVENT_COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
        EVENT_COUNTER.set(0);
    }

}
