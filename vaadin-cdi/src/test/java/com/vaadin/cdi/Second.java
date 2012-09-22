package com.vaadin.cdi;

import org.jboss.arquillian.drone.api.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: adam-bien.com
 */

@Qualifier
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Second {
}
