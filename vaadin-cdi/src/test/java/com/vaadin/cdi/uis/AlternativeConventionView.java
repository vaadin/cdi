package com.vaadin.cdi.uis;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AlternativeConventionView extends CustomComponent implements View {

	public static final String LABEL = "UNIQUELABEL";

	@Override
	public void enter(ViewChangeEvent event) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setCompositionRoot(layout);
		Label label = new Label(LABEL);
		label.setId("label");
		layout.addComponent(label);
	}
}
