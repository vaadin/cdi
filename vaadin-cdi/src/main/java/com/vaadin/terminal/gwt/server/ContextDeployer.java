package com.vaadin.terminal.gwt.server;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import com.vaadin.Application;
import com.vaadin.cdi.VaadinApplication;
import com.vaadin.cdi.VaadinCDIRootDiscoveryApplication;
import com.vaadin.cdi.VaadinRoot;
import com.vaadin.ui.Root;

@WebListener
public class ContextDeployer implements ServletContextListener {

	@Inject
	@VaadinApplication
	private Instance<Application> applications;

	private Set<String> applicationMappings;

	@Inject
	@VaadinRoot
	private Instance<Root> roots;

	private Set<String> rootMappings;

	@Inject
	private Instance<VaadinCDIApplicationServlet> servletInstanceProvider;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		applicationMappings = new HashSet<String>();
		rootMappings = new HashSet<String>();

		ServletContext context = sce.getServletContext();

		System.out.println("Initializing web context for path "
				+ context.getContextPath());

		discoverApplicationMappingsAndThrowOnConflicts();
		discoverRootMappingsAndThrowOnConflicts();

		registerVaadinApplications(context);
	}

	/**
	 * Checks whether there are multiple Vaadin application in class path
	 * annotated with @VaadinApplication annotation using same mapping.
	 */
	private void discoverApplicationMappingsAndThrowOnConflicts() {
		for (Application application : applications) {
			if (!VaadinCDIRootDiscoveryApplication.class
					.isAssignableFrom(application.getClass())) {
				VaadinApplication applicationAnnotation = application
						.getClass().getAnnotation(VaadinApplication.class);

				String mapping = applicationAnnotation.mapping();

				if (!mapping.startsWith("/")) {
					throw new RuntimeException("Mapping for application "
							+ application.getClass().getSimpleName()
							+ " does not start with /");
				}

				if (applicationMappings.contains(mapping)) {
					throw new RuntimeException(
							"Multiple Vaadin applications annotated with @VaadinApplication have same mapping attribute value or no mapping specified.");
				}

				applicationMappings.add(mapping);
			}
		}
	}

	/**
	 * Checks that there are no conflicting mapping in roots for default
	 * application
	 */
	private void discoverRootMappingsAndThrowOnConflicts() {
		for (Root root : roots) {

			VaadinRoot rootAnnotation = root.getClass().getAnnotation(
					VaadinRoot.class);
			String mapping = rootAnnotation.mapping();

			if (rootMappings.contains(mapping)) {
				throw new RuntimeException(
						"Multiple roots annotated with @VaadinRoot have same mapping or no mapping specified \""
								+ mapping + "\"");
			}

			rootMappings.add(mapping);
		}

		System.out
				.println("Following roots are available for default application: "
						+ rootMappings);
	}

	/**
	 * Registers all discovered applications to given servlet context
	 * 
	 * @param context
	 */
	private void registerVaadinApplications(ServletContext context) {
		for (Application vaadinApplication : applications) {
			registerApplicationToContext(vaadinApplication, context);
		}
	}

	/**
	 * Registers given application to given servletContext
	 * 
	 * @param vaadinApplication
	 * @param context
	 */
	private void registerApplicationToContext(Application vaadinApplication,
			ServletContext context) {
		String applicationName = getMappingForApplication(vaadinApplication);
		String className = vaadinApplication.getClass().getSimpleName();

		System.out.println("Instantiating new servlet for " + className);

		ServletRegistration.Dynamic registration = context.addServlet(
				className, servletInstanceProvider.get());

		registration.setInitParameter("application", vaadinApplication
				.getClass().getCanonicalName());

		registration.addMapping("/VAADIN/*");

		if (isDefaultApplication(vaadinApplication)) {
			if (!isSomeApplicationMappedToContextRoot()) {
				addMappingToRegistration("/*", registration);
			} else {
				addMappingToRegistration("/default/*", registration);
			}
		} else {
			addMappingToRegistration(applicationName, registration);
		}
	}

	private boolean isDefaultApplication(Application vaadinApplication) {
		return VaadinCDIRootDiscoveryApplication.class
				.isAssignableFrom(vaadinApplication.getClass());
	}

	private boolean isSomeApplicationMappedToContextRoot() {
		return applicationMappings.contains("/*");
	}

	/**
	 * Adds given mapping to given servlet registration
	 * 
	 * @param mapping
	 * @param registration
	 */
	private void addMappingToRegistration(String mapping, Dynamic registration) {
		System.out.println("Mapping " + registration.getName() + " to "
				+ mapping);
		registration.addMapping(mapping);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Context destroyed");
	}

	/**
	 * @param application
	 * @return intended URL mapping for given application
	 */
	private String getMappingForApplication(Application application) {
		Class<? extends Application> applicationClass = application.getClass();

		if (applicationClass.isAnnotationPresent(VaadinApplication.class)) {
			VaadinApplication deploymentIdentifier = applicationClass
					.getAnnotation(VaadinApplication.class);
			String mappingAttribute = deploymentIdentifier.mapping();

			if (mappingAttribute != null) {
				return mappingAttribute;
			}
		}
		return applicationClass.getSimpleName();
	}
}
