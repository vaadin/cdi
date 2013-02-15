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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;

/**
 * UIs annotated with
 * <code>&#064;Root</code> are bound to the context path of the application.
 * There can be only one UI class with this annotation in a web application.<p>
 * <pre>
 * <code>&#064;Root
 * &#064;VaadinUI
 * public class EntryPoint extends UI {}</code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Root {

    /**
     * The URL mapping of the Vaadin Servlet. By default, this is "/*".
     */
    @Nonbinding
    String urlMapping() default "/*";
}
