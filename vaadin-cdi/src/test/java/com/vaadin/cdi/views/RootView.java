package com.vaadin.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIView("")
public class RootView extends CustomComponent implements View {

    public static final String CONSTRUCT_COUNT = "RootViewConstruct";
    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);
        Label label = new Label("default view");
        label.setId("view");
        layout.addComponent(label);
    }

}
