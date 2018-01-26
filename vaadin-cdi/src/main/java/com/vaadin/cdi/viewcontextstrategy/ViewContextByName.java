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

import java.lang.annotation.*;

import static com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * Strategy to hold the context open while view name does not change.
 * <p>
 * This strategy is not on par with navigator view life cycle. While navigating
 * to same view, same context remains active.
 * {@link com.vaadin.navigator.View#enter(ViewChangeEvent)} will be called again
 * on the same view instance.
 * <p>
 * <strong>Note:</strong> Navigator view change events do not mean that the view
 * context has changed.
 *
 * @see ViewContextStrategy
 * @see ViewContextByNameAndParameters
 * @see ViewContextByNavigation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@ViewContextStrategyQualifier
public @interface ViewContextByName {
}
