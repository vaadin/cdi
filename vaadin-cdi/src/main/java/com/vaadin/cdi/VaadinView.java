package com.vaadin.cdi;

import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Similar semantics to
 *
 * @see javax.inject.Named
 *
 * @author adam-bien.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface VaadinView {

    /**
     *
     * The name of the VaadinView can be derived from the simple class name So
     * it is optional. Also multiple views without a value may exist at the same
     * time
     */
    public String value() default "";

    /**
     * Specifies whether view parameters can be passed to the view as part of
     * the name, i.e in the form of {@code viewName/viewParameters}. Make sure
     * there are no other views that start with the same name, since the
     * ViewProvider will only check that the given {@code viewAndParameters}
     * starts with the view name.
     */
    public boolean supportsParameters() default false;

    // why not @RollesAllowed?
    public String[] rolesAllowed() default {};

    public Class<? extends UI> ui() default UI.class;
}
