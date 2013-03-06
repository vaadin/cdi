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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

/**
 * All UIs need to be declared with this annotation. CDIUI annotation binds the
 * lifecycle of a given UI to Vaadin's view lifecycle. There is one UI instance
 * per tab and so multiple instances per session.
 * 
 */
@Stereotype
@UIScoped
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface CDIUI {

    /**
     * An optional URI mapping. If not specified, the mapping is going to be
     * derived from the simple name of the class. A class WelcomeVaadin is going
     * to be bound to "/welcomeVaadin" uri.
     * 
     * @return the URI mapping of this UI
     */
    public String value() default "";
}
