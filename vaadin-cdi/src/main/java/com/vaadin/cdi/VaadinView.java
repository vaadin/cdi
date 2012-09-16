package com.vaadin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.ui.UI;

/**
 * Similar semantics to @see javax.inject.Named
 * 
 * @author adam-bien.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface VaadinView {
    /**
     * 
     * The name of the VaadinView can be derived from the simple class name So
     * it is optional. Also multiple views without a value may exist at the same
     * time
     */
    public String value() default "";

    // why not @RollesAllowed?
    public String[] rolesAllowed() default {};

    public Class<? extends UI> ui() default UI.class;
}
