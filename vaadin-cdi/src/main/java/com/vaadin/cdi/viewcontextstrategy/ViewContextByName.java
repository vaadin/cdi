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

import java.lang.annotation.*;

import static com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * Strategy to hold the context open while view name does not change.
 * <p>
 * This strategy is not on par with navigator view life cycle. While navigating
 * to same view, same context remains active.
 * {@link com.vaadin.navigator.View#enter(ViewChangeEvent)} will be called again
 * on the same view instance.
 * <p>
 * <strong>Note:</strong> Navigator view change events do not mean that the view
 * context has changed.
 *
 * @see ViewContextStrategy
 * @see ViewContextByNameAndParameters
 * @see ViewContextByNavigation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@ViewContextStrategyQualifier
public @interface ViewContextByName {
}
