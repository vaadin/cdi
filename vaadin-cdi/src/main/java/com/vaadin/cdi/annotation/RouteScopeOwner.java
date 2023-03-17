/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.annotation;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Link a {@link RouteScoped @RouteScoped},
 * or {@link NormalRouteScoped @NormalRouteScoped} bean to its owner.
 * <p>
 * Owner is a router component.
 * A {@link Route @Route}, or a {@link RouterLayout}, or a {@link HasErrorParameter}.
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface RouteScopeOwner {
    /**
     * Owner class of the qualified {@link RouteScoped @RouteScoped},
     * or {@link NormalRouteScoped @NormalRouteScoped} bean.
     * <p>
     * A {@link Route @Route}, or a {@link RouterLayout}, or a {@link HasErrorParameter}
     *
     * @return owner class
     */
    Class<? extends HasElement> value();
}
