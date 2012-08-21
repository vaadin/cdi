package com.vaadin.reports.presentation;

import java.util.Date;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinRoot;
import com.vaadin.reports.business.issues.boundary.BugTracking;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

@VaadinRoot(mapping = "listing", application = ReportsApplication.class)
public class ReportsRoot extends Root {

	@Inject
	BugTracking bt;

	@Override
	protected void init(WrappedRequest request) {
		addComponent(new Button("New Bug", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				bt.fileBug("New bug: " + new Date());
			}
		}));
	}
}
