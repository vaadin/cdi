package com.vaadin.customer.presentation.view;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@VaadinView(name = "editor")
public class CustomerEditorView extends VerticalLayout implements View {

    public CustomerEditorView() {
        addComponent(new Label("Customer editor"));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
