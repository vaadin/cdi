package com.vaadin.cdi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VaadinSessionScopeActivationPolicy {

	/**
	 * Default value for {@link #strict()}
	 */
	boolean DEFAULT_STRICT = false;

	/**
	 * Determines if the session scope should check the session lock to activate the Scope.
	 * @return true if strict session locking should be enabled, false otherwise
	 */
	boolean strict() default DEFAULT_STRICT;
}
