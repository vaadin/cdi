package com.vaadin.purchases.presentation;

import com.vaadin.cdi.Mapping;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@Mapping(value = "purchases")
@VaadinUI
public class PurchasesRoot extends UI {

    @Override
    protected void init(WrappedRequest request) {
        addComponent(new Label("Purchases root"));
    }
}
