package com.vaadin.cdi.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy;
import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy.Policy;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.*;

/**
 * Utility class to get the VaadinSessionScopeActivationPolicy for a VaadinService.
 */
public class VaadinSessionActivationPolicyHolder {

	/**
	 * Caches the Policies for each VaadinService.
	 */
	private static final ConcurrentHashMap<String, Policy> policyCache = new ConcurrentHashMap<>();

	/**
	 * Get the VaadinSessionScopeActivationPolicy for the current VaadinService.
	 * @param vaadinService the current VaadinService
	 * @return the determined policy
	 */
	public static Policy get(final VaadinService vaadinService) {
		if (vaadinService == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final String servletName = isServiceInitialized(vaadinService) ? vaadinService.getServiceName() : null;
		if (servletName == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		return policyCache.computeIfAbsent(servletName, s -> initializePolicy(vaadinService));
	}

	/**
	 * Get the VaadinSessionScopeActivationPolicy for the current VaadinSession.
	 * @param session the current VaadinSession
	 * @return the determined policy
	 */
	public static Policy get(final VaadinSession session) {
		if (session == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		return get(session.getService());
	}


	/**
	 * Initialize the policy for the given VaadinService.
	 * @param vaadinService the VaadinService to initialize the policy for
	 * @return the policy
	 */
	private static Policy initializePolicy(final VaadinService vaadinService) {
		final String servletName = isServiceInitialized(vaadinService) ? vaadinService.getServiceName() : null;
		if (servletName == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		vaadinService.addServiceDestroyListener(event -> policyCache.remove(servletName));
		return determinePolicy(vaadinService);
	}


	/**
	 * Check if the VaadinService is initialized.
	 * @param vaadinService the VaadinService to check
	 * @return true if the VaadinService is initialized, false otherwise
	 */
	private static boolean isServiceInitialized(final VaadinService vaadinService) {
		if (vaadinService instanceof final VaadinServletService servletService) {
			final VaadinServlet servlet = servletService.getServlet();
			return servlet.getServletConfig() != null;
		} else {
			return false;
		}
	}

	/**
	 * Determine the VaadinSessionScopeActivationPolicy for the current VaadinService.
	 * @param vaadinService the current VaadinService
	 * @return the determined policy
	 */
	private static Policy determinePolicy(final VaadinService vaadinService) {
		if (vaadinService == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final VaadinContext context = vaadinService.getContext();
		if (context == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final AppShellRegistry registry = AppShellRegistry.getInstance(context);
		if (registry == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final Class<? extends AppShellConfigurator> configurator = registry.getShell();
		if (configurator == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		if (configurator.isAnnotationPresent(VaadinSessionScopeActivationPolicy.class)) {
			return configurator.getAnnotation(VaadinSessionScopeActivationPolicy.class).value();
		}
		return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
	}

}
