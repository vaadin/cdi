/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.cdi.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import com.vaadin.cdi.VaadinUI;

/**
 * Datastructure for storing bean instances in {@link VaadinUI} context.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public class UIBeanStore implements Serializable {

    private final Map<Contextual<?>, UIBeanStore.ContextualInstance<?>> instances = new HashMap<Contextual<?>, UIBeanStore.ContextualInstance<?>>();

    public UIBeanStore() {
        getLogger().info("Creating new UIBeanStore " + this);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanInstance(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        getLogger().info(
                "Getting bean instance for " + contextual + " from " + this);

        UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(contextual);

        if (contextualInstance == null && creationalContext != null) {
            contextualInstance = new UIBeanStore.ContextualInstance<T>(
                    contextual.create(creationalContext), creationalContext);
            instances.put(contextual, contextualInstance);
        }

        return contextualInstance != null ? contextualInstance.getInstance()
                : null;
    }

    public void dereferenceAllBeanInstances() {
        for (final Contextual<?> bean : new HashSet<Contextual<?>>(
                instances.keySet())) {
            dereferenceBeanInstance(bean);
        }
    }

    public <T> void dereferenceBeanInstance(final Contextual<T> bean) {
        @SuppressWarnings("unchecked")
        final UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(bean);
        if (contextualInstance != null) {
            bean.destroy(contextualInstance.getInstance(),
                    contextualInstance.getCreationalContext());
            instances.remove(bean);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIBeanStore.class.getCanonicalName());
    }

    class ContextualInstance<T> {

        private final T instance;
        private final CreationalContext<T> creationalContext;

        public ContextualInstance(final T instance,
                final CreationalContext<T> creationalContext) {
            super();
            this.instance = instance;
            this.creationalContext = creationalContext;
        }

        public T getInstance() {
            return instance;
        }

        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
    }
}
