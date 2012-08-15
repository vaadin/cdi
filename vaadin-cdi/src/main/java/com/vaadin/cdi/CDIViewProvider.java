package com.vaadin.cdi;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

public class CDIViewProvider implements ViewProvider {

	@Inject
	@Any
	private Instance<View> views;

	@Override
	public String getViewName(String viewAndParameters) {
		String viewName = parseViewName(viewAndParameters);
		Instance<View> availableViews = discoverViewsByAnnotation(viewName);

		if (availableViews.isUnsatisfied()) {
			return null;
		}

		if (availableViews.isAmbiguous()) {
			throw new RuntimeException(
					"CDIViewProvider has multiple choises for view with name "
							+ viewName);
		}

		return viewName;
	}

	@Override
	public View getView(String viewName) {
		Instance<View> view = discoverViewsByAnnotation(viewName);
		return view.get();
	}

	private String parseViewName(String viewAndParameters) {
		if (viewAndParameters.startsWith("!")) {
			return viewAndParameters.substring(1);
		}

		return viewAndParameters;
	}

	private Instance<View> discoverViewsByAnnotation(String viewName) {
		return views.select(new VaadinViewAnnotation(viewName));
	}
}
