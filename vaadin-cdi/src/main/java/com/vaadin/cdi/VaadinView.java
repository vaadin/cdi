/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.ui.UI;

/**
 * Similar semantics to
 * 
 * @see javax.inject.Named
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
