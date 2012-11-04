/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import java.lang.annotation.*;

import javax.inject.Scope;


/**
 * All UIs need to be declared with this annotation. VaadinUI annotation binds
 * the lifecycle of a given UI to Vaadin's view lifecycle. There is one UI
 * instance per tab and so multiple instances per session.
 * 
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface VaadinUI {
    /**
     * An optional URI mapping. If not specified, the mapping is going to be
     * derived from the simple name of the class. A class WelcomeVaadin is going
     * to be bound to "/welcomeVaadin" uri.
     * 
     * @return the URI mapping of this UI
     */
    public String value() default "";
}
