package com.vaadin.standard;

import com.vaadin.cdi.VaadinRoot;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

@VaadinRoot(mapping = "sales")
public class SalesMainRoot extends Root {

	@Override
	protected void init(WrappedRequest request) {
		addComponent(new Label("Sales main"));

	}
}
