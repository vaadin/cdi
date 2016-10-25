package com.vaadin.cdi.uis;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class UnannotatedUI extends UI {
	@Override
	protected void init(VaadinRequest request) {
		setContent(new Label("Unmapped"));
	}
}
