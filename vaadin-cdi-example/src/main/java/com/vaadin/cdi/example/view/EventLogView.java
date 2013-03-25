package com.vaadin.cdi.example.view;

import javax.enterprise.event.Observes;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.example.logging.LoggableEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(value = EventLogView.VIEW_ID)
public class EventLogView extends AbstractView {

    public static final String VIEW_ID = "events";

    private VerticalLayout layout = new VerticalLayout(new Label("Event log:"));
    private long startTime = System.currentTimeMillis();

    @Override
    protected Component buildContent() {
        layout.setSizeUndefined();

        return layout;
    }

    protected void logEvent(@Observes
    LoggableEvent event) {
        layout.addComponent(new Label(""
                + (System.currentTimeMillis() - startTime) + "ms: "
                + event.getValue()));
    }

}
