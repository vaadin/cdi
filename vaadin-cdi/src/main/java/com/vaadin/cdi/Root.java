package com.vaadin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


/**
 * UIs annotated with @Root are bound to the context path of the application.
 * There can be only one UI annotated with @Root per application.
 * 
 * <pre>
 *     <code>
 *  @Root
 *  @VaadinUI
 *  public class EntryPoint extends UI {}
 *     </code>
 * </pre>
 * 
 * @author adam-bien.com
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Root {
}
