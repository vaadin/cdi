package com.vaadin.purchases.presentation;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@VaadinUI(mapping = "purchases")
public class PurchasesRoot extends UI {

    @Override
    protected void init(WrappedRequest request) {
        addComponent(new Label("Purchases root"));
    }
}
