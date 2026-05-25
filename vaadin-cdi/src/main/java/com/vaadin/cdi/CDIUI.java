/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.inject.Stereotype;

/**
 * All UIs need to be declared with this annotation. CDIUI annotation binds the
 * lifecycle of a given UI to Vaadin's view lifecycle. There is one UI instance
 * per tab and so multiple instances per session.
 * 
 */
@Stereotype
@UIScoped
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface CDIUI {

    /**
     * An optional URI mapping. If not specified, the mapping is going to be
     * derived from the simple name of the class. A class WelcomeVaadin is going
     * to be bound to "/welcome-vaadin". A class SampleCDIApplicationUI will be
     * bound to "/sample-cdi-application".
     * 
     * Passing an empty string as the value will be interpreted as the root of
     * the application.
     * 
     * @return the URI mapping of this UI
     */
    public String value() default USE_CONVENTIONS;

    /**
     * USE_CONVENTIONS is treated as a special case that will cause the
     * automatic UI mapping to occur.
     */
    public final static String USE_CONVENTIONS = "USE CONVENTIONS";
}
