package com.vaadin.cdi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the activation policy for the VaadinSessionScopedContext.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VaadinSessionScopeActivationPolicy {

	/**
	 * Activation policies for the VaadinSessionScopedContext.
	 */
	enum Policy {
		STRICT,
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
