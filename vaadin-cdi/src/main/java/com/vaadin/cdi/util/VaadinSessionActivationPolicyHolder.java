package com.vaadin.cdi.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy;
import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy.Policy;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellRegistry;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

/**
 * Utility class to get the VaadinSessionScopeActivationPolicy for the current VaadinService.
 */
public class VaadinSessionActivationPolicyHolder {

	/**
	 * Caches the Policies for each VaadinService.
	 */
	private static final ConcurrentHashMap<String, PolicyWrapper> policyCache = new ConcurrentHashMap<>();

	/**
	 * Get the VaadinSessionScopeActivationPolicy for the current VaadinService.
	 * @param vaadinService the current VaadinService
	 * @return the determined policy
	 */
	public static Policy get(final VaadinService vaadinService) {
		if (vaadinService == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final PolicyWrapper wrapper = policyCache.computeIfAbsent(vaadinService.getServiceName(), s -> initializePolicy(vaadinService));
		return wrapper.policy;
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

	private static PolicyWrapper initializePolicy(final VaadinService vaadinService) {
		vaadinService.addServiceDestroyListener(event -> policyCache.remove(vaadinService.getServiceName()));
		return new PolicyWrapper(determinePolicy(vaadinService));
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

	/**
	 * Wrapper for the VaadinSessionScopeActivationPolicy.
	 * @param policy the policy
	 */
	private record PolicyWrapper(Policy policy) {
	}

}
