package com.vaadin.reports.presentation;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

//@VaadinApplication(mapping = "/reports/*")
public class ReportsApplication extends Application {

	@Inject
	private Instance<ReportsRoot> root;

	@Override
	protected Root getRoot(WrappedRequest request)
			throws RootRequiresMoreInformationException {
		return root.get();
	}
}
