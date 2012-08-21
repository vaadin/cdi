package com.vaadin.cdi;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.terminal.RootProvider;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public class CDIRootProvider implements RootProvider {

	@Inject
	@Any
	private Instance<Root> roots;

	@Override
	public Class<? extends Root> getRootClass(Application application,
			WrappedRequest request) throws RootRequiresMoreInformationException {
		String rootMapping = parseRootMapping(request);
		Root root = selectRootMatchingAnnotation(new VaadinRootAnnotation(
				application.getClass(), rootMapping));

		if (root != null) {
			return root.getClass();
		}

		return null;
	}

	@Override
	public Root instantiateRoot(Application application,
			Class<? extends Root> type, WrappedRequest request) {
		String rootMapping = parseRootMapping(request);
		Root root = selectRootMatchingAnnotation(new VaadinRootAnnotation(
				application.getClass(), rootMapping));

		if (root != null) {
			return root;
		}

		throw new RuntimeException("Could not instantiate root");
	}

	private Root selectRootMatchingAnnotation(VaadinRoot vaadinRoot) {
		Instance<Root> selectedRoot = roots.select(vaadinRoot);

		if (selectedRoot.isUnsatisfied()) {
			System.out.println("Could not find root");
			return null;
		}

		if (selectedRoot.isAmbiguous()) {
			System.out.println("Ambiguous root definition");
			return null;
		}

		return selectedRoot.get();
	}

	private String parseRootMapping(WrappedRequest request) {
		String requestPath = request.getRequestPathInfo();
		if (requestPath != null && requestPath.length() > 1) {
			if (requestPath.endsWith("/")) {
				return requestPath.substring(1, requestPath.lastIndexOf("/"));
			} else {
				return requestPath.substring(1);
			}
		}
		return "";
	}
}
