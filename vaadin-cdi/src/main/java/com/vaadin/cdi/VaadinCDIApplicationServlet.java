package com.vaadin.cdi;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

public class VaadinCDIApplicationServlet extends AbstractApplicationServlet {

	@Inject
	@Any
	private Instance<Application> applications;

	private Class<Application> vaadinApplicationClass;

	@Inject
	private Instance<CDIUIProvider> cdiRootProvider;

	@Override
	public void init() throws ServletException {
		super.init();

		String applicationClassName = getInitParameter("application");

		System.out.println("Initializing servlet for application "
				+ applicationClassName);

		try {

			// Get the application class this servlet is providing.
			vaadinApplicationClass = (Class<Application>) getServletContext()
					.getClassLoader().loadClass(applicationClassName);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find application class for "
					+ applicationClassName);
			throw new ServletException(e);
		}
	}

	@Override
	protected Application getNewApplication(HttpServletRequest request)
			throws ServletException {

		Application application = null;

		// If we're using default application we do not need to create the
		// instance with CDI
		if (Application.class.equals(vaadinApplicationClass)) {
			try {
				application = vaadinApplicationClass.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(
						"Failed to create application instance", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Failed to create application instance", e);
			}
		} else {
			// Find the application served by this servlet
			Instance<Application> filtered = applications
					.select(vaadinApplicationClass);

			if (filtered.isUnsatisfied()) {
				throw new ServletException(
						"No Vaadin application bean found for class: "
								+ vaadinApplicationClass.getName());
			}

			if (filtered.isAmbiguous()) {
				throw new ServletException(
						"More than one type of applications available after filtering");
			}

			application = filtered.get();
		}

		application.addUIProvider(cdiRootProvider.get());

		return application;
	}

	@Override
	public Class<? extends Application> getApplicationClass() {
		return vaadinApplicationClass;
	}
}