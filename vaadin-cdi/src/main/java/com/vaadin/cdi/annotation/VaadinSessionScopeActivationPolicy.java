package com.vaadin.cdi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.cdi.context.VaadinSessionScopedContext;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.VaadinSession;

/**
 * Annotation to specify the activation policy for the
 * {@link VaadinSessionScopedContext}.
 * <p>
 * Place this annotation on your {@link AppShellConfigurator}
 * implementation to control whether {@link VaadinSessionScoped} context requires
 * the current {@link VaadinSession} to be locked to
 * be considered active.
 * <ul>
 *     <li>{@link Policy#LENIENT} (default) – context is active as long as a
 *         {@link VaadinSession} is available on the current thread, regardless of
 *         whether the session is locked.</li>
 *     <li>{@link Policy#STRICT} – context is active only if a {@link VaadinSession}
 *         is available <em>and</em> currently locked by the calling thread.</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @VaadinSessionScopeActivationPolicy(Policy.STRICT)
 * public class AppShell implements AppShellConfigurator {
 *     // ...
 * }
 * }</pre>
 *
 * If the annotation is not present, {@link #DEFAULT_POLICY} is used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VaadinSessionScopeActivationPolicy {

	/**
	 * Activation policies for the VaadinSessionScopedContext.
	 */
	enum Policy {
		/**
		 * Context is active only if a {@link VaadinSession} is available <em>and</em> currently locked by the calling thread.
		 */
		STRICT,
		/**
		 * Context is active as long as a {@link VaadinSession} is available on the current thread, regardless of whether the session is locked.
		 */
		LENIENT
	}

	/**
	 * Default policy for the VaadinSessionScopedContext.
	 */
	Policy DEFAULT_POLICY = Policy.LENIENT;

	/**
	 * Determines if the session scope should check the session lock to activate the Scope.
	 * @return Policy indicating the activation policy for VaadinSessionScoped beans
	 */
	Policy value() default Policy.LENIENT;
}
