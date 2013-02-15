/*
 * Copyright 2013 Vaadin Ltd.
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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.context.NormalScope;

/**
 * Specifies that a bean is scoped to a Vaadin {@link com.vaadin.ui.UI}. This
 * scope is active every time {@link com.vaadin.ui.UI#getCurrent() } returns a
 * non-null value. <p> Please note that no {@link com.vaadin.ui.UI}s may be
 * annotated with this annotation. They should use the {@link VaadinUI}
 * annotation instead.
 */
@NormalScope(passivating = true)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Documented
@Inherited
public @interface UIScoped {
}
