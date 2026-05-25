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

import com.vaadin.cdi.internal.ViewContextStrategies.ViewNameAndParameters;

import java.lang.annotation.*;

/**
 * Strategy to release, and create a new context on every navigation regardless
 * of view name and parameters.
 * <p>
 * It is on par with navigator view life cycle, but navigating to same view with
 * same parameters releases the context and creates a new one.
 * <p>
 * In practice it works same as {@link ViewNameAndParameters}, even when
 * parameters does not change.
 *
 * @see ViewContextStrategy
 * @see ViewContextByName
 * @see ViewContextByNameAndParameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@ViewContextStrategyQualifier
public @interface ViewContextByNavigation {
}
