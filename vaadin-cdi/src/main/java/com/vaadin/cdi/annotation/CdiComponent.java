/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.cdi.annotation;

import javax.enterprise.inject.Stereotype;
import java.lang.annotation.*;

/**
 * This is a {@code @Stereotype} to let the container scan and manage the
 * instances of the annotated type.
 * <p>
 * When the bean-discovery-mode="annotated" is set in the beans.xml file
 * (implicit bean archive), CDI can only manage and inject beans annotated
 * with a "scope" type.
 * See https://docs.jboss.org/cdi/spec/1.2/cdi-spec.html#normal_scope for
 * more information about scopes and pseudo-scopes.
 * <p>
 * If the explicit bean archive strategy is in use (an empty beans.xml or the
 * one containing bean-discovery-mode="all"), CDI can manage
 * and inject any bean, except those annotated with @Vetoed.
 */
@Stereotype
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD,
        ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface CdiComponent {
}
