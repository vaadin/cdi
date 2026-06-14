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

import jakarta.inject.Scope;

/**
 * The lifecycle of a ViewScoped component starts when the view is navigated to
 * and ends when navigating to a different view or when the UI is closed.
 * <p>
 * Injection with this annotation will create a direct reference to the object
 * rather than a proxy.
 * <p>
 * There are some limitations when not using proxies. Interceptors and
 * decorators will not work. Circular referencing (that is, injecting A to B and
 * B to A) will not work unless there is at least one proxy in between.
 * <p>
 * The sister annotation to this is the {@link NormalViewScoped}. Both
 * annotations reference the same underlying scope, so it is possible to get
 * both a proxy and a direct reference to the same object by using different
 * annotations.
 */
@Scope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD,
        ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewScoped {
}
