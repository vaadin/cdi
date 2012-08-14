package com.vaadin.terminal.gwt.server;

import com.vaadin.Application;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class VaadinCDIApplicationServlet extends AbstractApplicationServlet {

	@Inject
	@Any
	private Instance<Application> applications;

	private Class<? extends Application> vaadinApplicationClass;

	@Override
	public void init() throws ServletException {
		super.init();

		String applicationClassName = getInitParameter("application");

		System.out.println("Initializing servlet for application "
				+ applicationClassName);

		try {

			// Get the application class this servlet is providing.
			vaadinApplicationClass = (Class<? extends Application>) getServletContext()
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
		// Find the application served by this servlet
		Instance<Application> filtered = (Instance<Application>) applications
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

		Application application = filtered.get();
		System.out.println("Instantiating new application " + application);
		
		return application;
	}

	@Override
	public Class<? extends Application> getApplicationClass() {
		return vaadinApplicationClass;
	}
}