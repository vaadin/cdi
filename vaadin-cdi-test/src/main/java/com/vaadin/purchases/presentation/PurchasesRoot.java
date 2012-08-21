package com.vaadin.purchases.presentation;

import com.vaadin.cdi.VaadinRoot;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

@VaadinRoot(mapping = "purchaseslisting", application = PurchasesApplication.class)
public class PurchasesRoot extends Root {

	@Override
	protected void init(WrappedRequest request) {
		addComponent(new Label("Purchases root"));
	}
}
