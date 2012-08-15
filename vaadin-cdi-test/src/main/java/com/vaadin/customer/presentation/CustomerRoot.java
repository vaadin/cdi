package com.vaadin.customer.presentation;

import javax.inject.Inject;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.VaadinRoot;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.SimpleViewDisplay;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

@VaadinRoot(mapping = "customers")
public class CustomerRoot extends Root {

	private Navigator navigator;

	@Inject
	private CDIViewProvider cdiViewProvider;

	@Override
	protected void init(WrappedRequest request) {
		SimpleViewDisplay viewDisplay = new SimpleViewDisplay();
		setContent(viewDisplay);

		navigator = new Navigator(getPage(), viewDisplay);
		navigator.registerProvider(cdiViewProvider);
	}
}
