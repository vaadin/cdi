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

import java.util.stream.Stream;

import jakarta.enterprise.inject.spi.Unmanaged;

import com.vaadin.cdi.util.BeanProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class InstantiatorAlternative implements Instantiator {

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        return Stream.of();
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        T instance = BeanProvider.getContextualReference(type, true);
        if (InstantiatorCustomizeView.class.equals(type)) {
            ((InstantiatorCustomizeView) instance).customize();
        }
        return instance;
    }

    @Override
    public <T extends Component> T createComponent(Class<T> componentClass) {
        Unmanaged<T> unmanagedClass = new Unmanaged<T>(componentClass);
        Unmanaged.UnmanagedInstance<T> instance = unmanagedClass.newInstance();
        instance.produce().inject().postConstruct();
        return instance.get();
    }
}
