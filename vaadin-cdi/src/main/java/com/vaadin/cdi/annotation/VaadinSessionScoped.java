/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.annotation;

import com.vaadin.flow.server.VaadinSession;

import javax.enterprise.context.NormalScope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The lifecycle of a VaadinSessionScoped bean is bound to a {@link VaadinSession}.
 * <p>
 * Injecting with this annotation will create a proxy for the contextual
 * instance rather than provide the contextual instance itself.
 * <p>
 * Contextual instances stored in {@link VaadinSession},
 * so indirectly stored in HTTP session.
 * {@link javax.annotation.PreDestroy} called after
 * {@link com.vaadin.flow.server.SessionDestroyEvent} fired.
 *
 * @since 3.0
 */
@NormalScope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface VaadinSessionScoped {
}
