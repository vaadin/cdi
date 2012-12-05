package com.vaadin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to specify the URL mapping of the Vaadin servlet.
 * Used in conjunction with a @Root annotation, as there may only be one @URLMapping
 * annotation per project.
 * 
 * @author Marcus Hellberg / Vaadin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface URLMapping {
	String value() default "/*";
}
