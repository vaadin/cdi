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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import java.lang.annotation.*;

/**
 * Strategy to hold the context open while view name and view parameters do not
 * change.
 * <p>
 * This strategy is on par with navigator view life cycle. If navigation is not
 * reverted in a {@link ViewChangeEvent#beforeViewChange(ViewChangeEvent)}, a
 * new view context is activated. After
 * {@link ViewChangeEvent#afterViewChange(ViewChangeEvent)} is called, old view
 * context will be closed.
 * <p>
 * {@link View#enter(ViewChangeEvent)} will be called for the new {@link View}
 * instance.
 *
 * @see ViewContextStrategy
 * @see ViewContextByName
 * @see ViewContextByNavigation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@ViewContextStrategyQualifier
public @interface ViewContextByNameAndParameters {
}
