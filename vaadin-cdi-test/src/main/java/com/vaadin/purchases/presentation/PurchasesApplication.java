package com.vaadin.purchases.presentation;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.cdi.VaadinApplication;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

@VaadinApplication
public class PurchasesApplication extends Application {

	@Inject
	private Instance<PurchasesRoot> root;

	@Override
	protected Root getRoot(WrappedRequest request)
			throws RootRequiresMoreInformationException {
		return root.get();
	}
}
