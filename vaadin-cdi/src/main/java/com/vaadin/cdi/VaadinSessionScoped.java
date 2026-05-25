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

import jakarta.enterprise.context.NormalScope;
import java.lang.annotation.*;

/**
 * The lifecycle of a VaadinSessionScoped bean is bound to a VaadinSession.
 * <p>
 * Injecting with this annotation will create a proxy for the contextual
 * instance rather than provide the contextual instance itself.
 * <p>
 * <p>
 * Contextual instances stored in VaadinSession, so indirectly stored in HTTP session.
 * {@link jakarta.annotation.PreDestroy} called after SessionDestroyEvent fired.
 * </p>
 * 
 * @since 3.0
 */
@NormalScope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface VaadinSessionScoped {
}
