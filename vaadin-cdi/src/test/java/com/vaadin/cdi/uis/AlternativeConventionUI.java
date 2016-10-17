package com.vaadin.cdi.uis;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AlternativeConventionUI extends UI {

	public static final String LABEL = "TESTLABEL";
	public static final String ID = "label";

	@Override
	protected void init(VaadinRequest request) {
		Layout layout = new VerticalLayout();
		final Label label = new Label("+AlternativeConventionUI");
		layout.addComponent(label);

		final Label id = new Label(LABEL);
		id.setId(ID);
		layout.addComponent(id);
		setContent(layout);

	}
}
