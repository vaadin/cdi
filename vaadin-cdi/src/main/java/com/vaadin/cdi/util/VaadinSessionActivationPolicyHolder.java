package com.vaadin.cdi.util;

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
	 * Get the VaadinSessionScopeActivationPolicy for the current VaadinService.
	 * @param vaadinService the current VaadinService
	 * @return the determined policy
	 */
	public static Policy get(final VaadinService vaadinService) {
		if (vaadinService == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final VaadinContext context = vaadinService.getContext();
		if (context == null) {
			return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
		}
		final PolicyWrapper attribute = context.getAttribute(PolicyWrapper.class, () -> new PolicyWrapper(determinePolicy(vaadinService)));
		return attribute.policy;
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
		PolicyWrapper wrapper = ensureLock(session, () -> session.getAttribute(PolicyWrapper.class));
		if (wrapper == null) {
			// We can do some performance optimization...
			final Policy policy = get(session.getService());
			wrapper = new PolicyWrapper(policy);
			final PolicyWrapper finalWrapper = wrapper;
			ensureLock(session, () -> session.setAttribute(PolicyWrapper.class, finalWrapper));
		}
		return wrapper.policy;
	}

	/**
	 * Ensures that the session has a lock before executing the supplier.
	 * @param session the session
	 * @param supplier the supplier
	 * @return the result of the supplier
	 * @param <T> the type of the result
	 */
	private static <T> T ensureLock(final VaadinSession session, final Supplier<T> supplier) {
		if (session.hasLock()) {
			return supplier.get();
		} else {
			final AtomicReference<T> result = new AtomicReference<>();
			session.accessSynchronously(() -> result.set(supplier.get()));
			return result.get();
		}
	}

	/**
	 * Ensures that the session has a lock before executing the runnable.
	 * @param session the session
	 * @param runnable the runnable
	 */
	private static void ensureLock(final VaadinSession session, final Runnable runnable) {
		if (session.hasLock()) {
			runnable.run();
		} else {
			session.accessSynchronously(runnable::run);
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

	/**
	 * Wrapper for the VaadinSessionScopeActivationPolicy.
	 * @param policy the policy
	 */
	private record PolicyWrapper(Policy policy) {
	}

}
