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
 *
 */

package com.vaadin.cdi.viewcontextstrategy;

import com.vaadin.cdi.internal.ViewContextStrategies.ViewNameAndParameters;

import java.lang.annotation.*;

/**
 * Strategy to release, and create a new context on every navigation regardless
 * of view name and parameters.
 * <p>
 * It is on par with navigator view life cycle, but navigating to same view with
 * same parameters releases the context and creates a new one.
 * <p>
 * In practice it works same as {@link ViewNameAndParameters}, even when
 * parameters does not change.
 *
 * @see ViewContextStrategy
 * @see ViewContextByName
 * @see ViewContextByNameAndParameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@ViewContextStrategyQualifier
public @interface ViewContextByNavigation {
}
