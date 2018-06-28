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
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;
import java.util.stream.Stream;

@Priority(Interceptor.Priority.APPLICATION)
@Alternative
@VaadinServiceEnabled
@VaadinServiceScoped
public class InstantiatorAlternative implements Instantiator {

    @Override
    public boolean init(VaadinService service) {
        return true;
    }

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
}
