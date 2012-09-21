package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author adam-bien.com
 */
@VaadinView
public class DependentInstrumentedView extends CustomComponent implements View {

    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();

    }

    @Override
    public void enter(ViewChangeEvent event) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);
        Label label = new Label("ViewLabel");
        label.setId("label");
        layout.addComponent(label);
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }

}
