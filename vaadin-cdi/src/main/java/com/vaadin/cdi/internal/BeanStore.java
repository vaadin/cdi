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
package com.vaadin.cdi.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * Container for beans that belong to a specific scope.
 */
class BeanStore implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(BeanStore.class.getCanonicalName());
    private final Map<Contextual<?>, SerializableContextualInstance<?>> instances = new HashMap<Contextual<?>, SerializableContextualInstance<?>>();

    BeanStore() {
    }

    /**
     * Returns the bean instance of the specified bean. If no bean instance is
     * found in the store, a new instance is created, stored and returned. If
     * the creational context is null and no existing bean is found, null is
     * returned.
     */
    <T> T getBeanInstance(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        assert contextual != null : "contextual must not be null";
        T instance = getBeanInstance(contextual);
        if (instance == null && creationalContext != null) {
            instance = contextual.create(creationalContext);
            registerBeanInstance(instance, contextual, creationalContext);
        }
        return instance;
    }

    private <T> T getBeanInstance(Contextual<T> contextual) {
        logger.log(Level.FINE, "Looking up bean instance of contextual {0}", contextual);
        SerializableContextualInstance<T> contextualInstance = (SerializableContextualInstance<T>) instances.get(contextual);
        return contextualInstance == null ? null : contextualInstance.instance;
    }

    private Collection<Contextual<?>> getAllBeans() {
        return instances.keySet();
    }

    private <T> void registerBeanInstance(T instance, Contextual<T> contextual, CreationalContext<T> creationalContext) {
        if (!isSerializable(instance)) {
            logger.log(Level.WARNING, "Bean instance {0} is not serializable", instance);
        }
        if (!isPassivationCapable(contextual)) {
            logger.log(Level.WARNING, "Contextual {0} is not passivation capable", contextual);
        }
        if (!isSerializable(creationalContext)) {
            logger.log(Level.WARNING, "Creational context {0} is not passivation capable", creationalContext);
        }
        SerializableContextualInstance<T> contextualInstance = new SerializableContextualInstance<T>(instance, creationalContext);
        instances.put(contextual, contextualInstance);
    }

    /**
     * Destroys all bean instances and removes them from the store.
     */
    void destroyAllBeanInstances() {
        for (Contextual<?> bean : new LinkedList<Contextual<?>>(getAllBeans())) {
            destroyBeanInstance(bean);
        }
    }

    /**
     * Destroys the specified bean instance and removes it from the store.
     */
    <T> void destroyBeanInstance(Contextual<T> bean) {
        assert bean != null : "bean must not be null";
        SerializableContextualInstance<T> contextualInstance = (SerializableContextualInstance<T>) instances.get(bean);
        if (contextualInstance != null) {
            logger.log(Level.FINE, "Destroying instance {0} of bean {1} using creational context {2}",
                    new Object[]{contextualInstance.instance, bean, contextualInstance.creationalContext});
            bean.destroy(contextualInstance.instance, contextualInstance.creationalContext);
            instances.remove(bean);
        }
    }

    private class SerializableContextualInstance<T> implements java.io.Serializable {

        final T instance;
        final CreationalContext<T> creationalContext;

        SerializableContextualInstance(final T instance,
                final CreationalContext<T> creationalContext) {
            this.instance = instance;
            this.creationalContext = creationalContext;
        }
    }

    private static boolean isPassivationCapable(Contextual<?> contextual) {
        return contextual instanceof PassivationCapable;
    }

    private static boolean isSerializable(Object o) {
        return o instanceof java.io.Serializable;
    }
}
