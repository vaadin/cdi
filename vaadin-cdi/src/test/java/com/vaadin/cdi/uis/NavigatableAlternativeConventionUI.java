package com.vaadin.cdi.uis;

import javax.inject.Inject;

import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class NavigatableAlternativeConventionUI extends UI {

	public static final String LABEL = "TESTLABEL";
	public static final String ID = "label";

	@Inject
	CDIViewProvider viewProvider;

	@Override
	protected void init(VaadinRequest request) {
		Navigator navigator = new Navigator(this, this);
		navigator.addProvider(viewProvider);

	}
}
