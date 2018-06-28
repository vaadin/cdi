/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.cdi.itest.instantiatorcustomize;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.NavigationEvent;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

@Priority(Interceptor.Priority.APPLICATION)
@Decorator
public abstract class InstantiatorDecorator implements Instantiator {
    @Inject
    @Delegate
    @VaadinServiceEnabled
    private Instantiator delegate;

    @Override
    public <T> T getOrCreate(Class<T> type) {
        T instance = delegate.getOrCreate(type);
        if (InstantiatorCustomizeView.class.equals(type)) {
            ((InstantiatorCustomizeView) instance).customize();
        }
        return instance;
    }

    @Override
    public <T extends HasElement> T createRouteTarget(Class<T> routeTargetType, NavigationEvent event) {
        // Need to override it too to make it work on both Weld and OWB.
        return getOrCreate(routeTargetType);
    }
}
