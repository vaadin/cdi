package com.vaadin.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import com.vaadin.Application;
import com.vaadin.ui.Root;

@WebListener
public class ContextDeployer implements ServletContextListener {

	private Map<String, Set<String>> rootMappings;

	@Inject
	@VaadinApplication
	private Instance<Application> applications;

	@Inject
	@Any
	private Instance<Root> roots;

	@Inject
	private Instance<VaadinCDIApplicationServlet> servletInstanceProvider;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		rootMappings = new HashMap<String, Set<String>>();

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
			VaadinApplication applicationAnnotation = application.getClass()
					.getAnnotation(VaadinApplication.class);

			String mapping = applicationAnnotation.mapping();

			if (!mapping.startsWith("/")) {
				throw new RuntimeException("Mapping for application "
						+ application.getClass().getSimpleName()
						+ " does not start with /");
			}

			if (rootMappings.containsKey(mapping)) {
				throw new RuntimeException(
						"Multiple Vaadin applications annotated with @VaadinApplication have same mapping attribute value or no mapping specified.");
			}

			rootMappings.put(mapping, new HashSet<String>());
		}
	}

	/**
	 * Checks that there are no multiple roots assigned to same application with
	 * same mapping
	 */
	private void discoverRootMappingsAndThrowOnConflicts() {
		for (Root root : roots) {

			if (root.getClass().isAnnotationPresent(VaadinRoot.class)) {
				VaadinRoot vaadinRootAnnotation = root.getClass()
						.getAnnotation(VaadinRoot.class);
				Class<? extends Application> applicationClass = vaadinRootAnnotation
						.application();

				String applicationMapping = "/*";
				String rootMapping = vaadinRootAnnotation.mapping();

				if (applicationClass
						.isAnnotationPresent(VaadinApplication.class)) {
					VaadinApplication vaadinApplicationAnnotation = applicationClass
							.getAnnotation(VaadinApplication.class);

					applicationMapping = vaadinApplicationAnnotation.mapping();
				}

				if (!rootMappings.containsKey(applicationMapping)) {
					rootMappings.put(applicationMapping, new HashSet<String>());
				}

				if (rootMappings.get(applicationMapping).contains(rootMapping)) {
					throw new RuntimeException("Application "
							+ applicationMapping
							+ " has multiple roots with same mapping "
							+ rootMapping);
				}

				rootMappings.get(applicationMapping).add(rootMapping);
			}
		}

		for (String applicationMapping : rootMappings.keySet()) {
			System.out.println(applicationMapping + " "
					+ rootMappings.get(applicationMapping));
		}
	}

	/**
	 * Registers all discovered applications to given servlet context
	 * 
	 * @param context
	 */
	private void registerVaadinApplications(ServletContext context) {
		if (rootMappings.isEmpty()) {
			System.out
					.println("Could not register Vaadin applications or Roots, no such classes found with @VaadinApplication or @VaadinRoot annotations");
		}

		if (isApplicationsWithAnnotationsSpecified()) {
			for (Application vaadinApplication : applications) {
				registerApplicationToContext(vaadinApplication, context);
			}
		}

		if (isRootsToDefaultApplicationSpecified()) {
			if (!isApplicationRegisteredToContextRoot(context)) {
				registerDefaultApplicationToContext(context);
			}
		}
	}

	private void registerDefaultApplicationToContext(ServletContext context) {
		registerApplicationToContext(Application.class, "/*", context);
	}

	private boolean isApplicationsWithAnnotationsSpecified() {
		return !applications.isUnsatisfied();
	}

	private boolean isRootsToDefaultApplicationSpecified() {
		if (rootMappings.containsKey("/*")) {
			return !rootMappings.get("/*").isEmpty();
		}

		return false;
	}

	private boolean isApplicationRegisteredToContextRoot(ServletContext context) {
		for (ServletRegistration registration : context
				.getServletRegistrations().values()) {
			if (registration.getMappings().contains("/*")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Registers given application to given servletContext
	 * 
	 * @param vaadinApplication
	 * @param context
	 */
	private void registerApplicationToContext(Application vaadinApplication,
			ServletContext context) {
		String mapping = getMappingForApplication(vaadinApplication);

		registerApplicationToContext(vaadinApplication.getClass(), mapping,
				context);
	}

	private void registerApplicationToContext(
			Class<? extends Application> applicationClass, String mapping,
			ServletContext context) {
		String className = applicationClass.getSimpleName();

		System.out.println("Instantiating new servlet for " + className);

		ServletRegistration.Dynamic registration = context.addServlet(
				className, servletInstanceProvider.get());

		registration.setInitParameter("application",
				applicationClass.getCanonicalName());

		registration.addMapping("/VAADIN/*");

		addMappingToRegistration(mapping, registration);
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
