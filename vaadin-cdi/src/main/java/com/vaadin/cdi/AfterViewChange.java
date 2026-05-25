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
package com.vaadin.cdi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.navigator.ViewChangeListener;

/**
 * {@code ViewChangeEvent} can be observed with this qualifier.
 * <p>
 * Observers are called after all non-cdi
 * {@link ViewChangeListener#afterViewChange(ViewChangeListener.ViewChangeEvent)}
 * listeners.
 * <p>
 * Keep in mind, context of new view is activated before event is fired.
 * Accessing any {@link NormalViewScoped} bean through
 * {@link ViewChangeListener.ViewChangeEvent#getOldView()} might lead to
 * unexpected result, because it is looked up in the new context.
 * <p>
 * Though, context of new view, and context of old view can be the same
 * according to selected {@link ViewContextStrategy}.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
public @interface AfterViewChange {
}
