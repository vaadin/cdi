/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.viewcontextstrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations which define a strategy to
 * drive the behavior of the view context have to be annotated with this.
 * <p>
 * Annotations for built-in strategies:
 * {@link ViewContextByNameAndParameters},
 * {@link ViewContextByName},
 * {@link ViewContextByNavigation}
 *
 * @see ViewContextStrategy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ViewContextStrategyQualifier {
}
