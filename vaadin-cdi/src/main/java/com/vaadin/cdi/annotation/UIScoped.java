/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.annotation;

import com.vaadin.flow.component.UI;

import javax.inject.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The lifecycle of a UIScoped component is bound to a browser tab.
 * <p>
 * Injection with this annotation will create a direct reference to the object
 * rather than a proxy.
 * <p>
 * There are some limitations when not using proxies. Circular referencing (that
 * is, injecting A to B and B to A) will not work.
 * Injecting into a larger scope will bind the instance
 * from the currently active smaller scope, and will ignore smaller scope change.
 * For example after being injected into session scope it will point to the same
 * {@link UIScoped} bean instance ( even its {@link UI} is closed )
 * regardless of {@link UI} change.
 * <p>
 * The sister annotation to this is the {@link NormalUIScoped}. Both annotations
 * reference the same underlying scope, so it is possible to get both a proxy
 * and a direct reference to the same object by using different annotations.
 */
@Scope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD,
        ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface UIScoped {
}
