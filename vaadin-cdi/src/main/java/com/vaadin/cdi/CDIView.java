/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import javax.enterprise.inject.Stereotype;

import com.vaadin.ui.UI;

/**
 * Similar semantics to
 * 
 * @see javax.inject.Named
 */
@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@UIScoped
public @interface CDIView {

    /**
     * 
     * The name of the CDIView can be derived from the simple class name So it
     * is optional. Also multiple views without a value may exist at the same
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

    /**
     * Specifies which UIs can show the view. {@link CDIViewProvider} only lists
     * the views that have the current UI on this list.
     * 
     * If this list contains UI.class, the view is available for all UIs.
     * 
     * This only needs to be specified if the application has multiple UIs that
     * use {@link CDIViewProvider}.
     * 
     * @return list of UIs in which the view can be shown.
     */
    public Class<? extends UI>[] uis() default { UI.class };
}
