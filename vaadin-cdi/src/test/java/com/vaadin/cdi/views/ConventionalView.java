package com.vaadin.cdi.views;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.uis.ParameterizedNavigationUI;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(uis = { ParameterizedNavigationUI.class })
@Dependent
public class ConventionalView extends CustomComponent implements View {
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
        Label label = new Label("conventional");
        label.setId("view");
        layout.addComponent(label);
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void reset() {
        COUNTER.set(0);
    }

}
