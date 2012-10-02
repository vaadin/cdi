package com.vaadin.purchases.presentation;

import com.vaadin.cdi.Mapping;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@Mapping(value = "purchases")
@VaadinUI
public class PurchasesRoot extends UI {

    @Override
    protected void init(VaadinRequest request) {
        addComponent(new Label("Purchases root"));
    }
}
