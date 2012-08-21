package com.vaadin.purchases.presentation;

import com.vaadin.Application;
import com.vaadin.cdi.VaadinApplication;

@VaadinApplication(mapping = "/purchases/*")
public class PurchasesApplication extends Application {

	@Override
	public void init() {
		super.init();
	}
}
