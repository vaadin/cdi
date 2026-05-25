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

import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategyQualifier;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByNameAndParameters;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import jakarta.enterprise.inject.Stereotype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes implementing {@link View} and annotated with <code>@CDIView</code>
 * are automatically registered with {@link CDIViewProvider} for use by
 * {@link CDINavigator}.
 * <p>
 * By default, the view name is derived from the class name of the annotated
 * class, but this can also be overridden by defining a {@link #value()}.
 * <p>
 * <code>@CDIView</code> views are by default {@link ViewScoped}.
 * <p>
 * On a <code>@CDIView</code> view the strategy for the view context
 * can be defined by annotating the view with a
 * {@link ViewContextStrategyQualifier} annotation.
 * By default it is {@link ViewContextByNameAndParameters}.
 *
 * @see jakarta.inject.Named
 */
@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@ViewScoped
public @interface CDIView {

    /**
     *
     * The name of the CDIView can be derived from the simple class name So it
     * is optional. Also multiple views without a value may exist at the same
     * time.
     * <p>
     * Example: UserDetailView by convention becomes "user-detail" and
     * UserCDIExample becomes "user-cdi-example".
     */
    public String value() default USE_CONVENTIONS;

    /**
     * USE_CONVENTIONS is treated as a special case that will cause the
     * automatic View mapping to occur.
     */
    public static final String USE_CONVENTIONS = "USE CONVENTIONS";

    /**
     * Specifies which UIs can show the view. {@link CDIViewProvider} only lists
     * the views that have the current UI on this list.
     * <p>
     * If this list contains UI.class, the view is available for all UIs.
     * <p>
     * This only needs to be specified if the application has multiple UIs that
     * use {@link CDIViewProvider}.
     *
     * @return list of UIs in which the view can be shown.
     */
    public Class<? extends UI>[] uis() default { UI.class };

}
