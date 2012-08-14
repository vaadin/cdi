package com.vaadin.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

@VaadinApplication
public final class VaadinCDIRootDiscoveryApplication extends Application {

	@Inject
	@VaadinRoot
	private Instance<Root> availableRoots;

	@Override
	protected Root getRoot(WrappedRequest request) {
		String rootName = getRootNameFromRequest(request);
		Map<String, Root> applicationRoots = discoverRootsForThisApplication();
		if (applicationRoots.containsKey(rootName)) {
			return applicationRoots.get(rootName);
		}
		throw new RuntimeException("Could not determine root " + rootName
				+ " for application ");
	}

	protected String getRootNameFromRequest(WrappedRequest request) {
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

	protected Map<String, Root> discoverRootsForThisApplication() {
		Map<String, Root> mappedRoots = new HashMap<String, Root>();
		for (Root root : availableRoots) {
			Class<? extends Root> rootClass = root.getClass();
			VaadinRoot deploymentIdentifier = rootClass
					.getAnnotation(VaadinRoot.class);
			String rootMapping = deploymentIdentifier.mapping();
			if (rootMapping == null) {
				rootMapping = "";
			}
			if (mappedRoots.containsKey(rootMapping)) {
				throw new RuntimeException(
						"Multiple roots for same application with same mapping");
			}
			mappedRoots.put(rootMapping, root);
		}
		return mappedRoots;
	}
}
