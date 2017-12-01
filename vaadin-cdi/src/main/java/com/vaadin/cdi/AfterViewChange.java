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

package com.vaadin.cdi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.vaadin.navigator.ViewChangeListener;

/**
 * {@code ViewChangeEvent} can be observed with this qualifier.
 * <p>
 * Observers are called after all non-cdi
 * {@link ViewChangeListener#afterViewChange(ViewChangeListener.ViewChangeEvent)}
 * listeners.
 * <p>
 * Keep in mind, context of new view is activated before event is fired.
 * Accessing any {@link NormalViewScoped} bean through
 * {@link ViewChangeListener.ViewChangeEvent#getOldView()} might lead to
 * unexpected result, because it is looked up in the new context.
 * <p>
 * Though, context of new view, and context of old view can be the same
 * according to {@link CDIView#contextStrategy()}.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
public @interface AfterViewChange {
}
