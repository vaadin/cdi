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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import java.lang.annotation.*;

/**
 * Strategy to hold the context open while view name and view parameters do
 * not change.
 * <p>
 * This strategy is on par with navigator view life cycle. If navigation is
 * not reverted in a
 * {@link ViewChangeEvent#beforeViewChange(ViewChangeEvent)}, a new view
 * context is activated. After
 * {@link ViewChangeEvent#afterViewChange(ViewChangeEvent)} is called, old
 * view context will be closed.
 * <p>
 * {@link View#enter(ViewChangeEvent)} will be called for the new
 * {@link View} instance.
 *
 * @see ViewContextStrategy
 * @see ViewNameDriven
 * @see EveryNavigationDriven
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@ViewContextStrategyQualifier
public @interface ViewNameAndParametersDriven {
}


