package com.vaadin.customer.presentation.view;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@VaadinView("info")
public class CustomerMainView extends VerticalLayout implements View {

    public CustomerMainView() {
        addComponent(new Label("Customer info"));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
