package com.vaadin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface VaadinView {
    /**
     * 
     * The name of the VaadinView can be derived from the simple class name
     * So it is optional. 
     * Also multiple views without a value may exist at the same time
     */
    @Nonbinding
    public String value() default "";
}
