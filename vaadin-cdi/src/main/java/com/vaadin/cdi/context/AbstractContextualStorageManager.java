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

package com.vaadin.cdi.context;

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualInstanceInfo;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.RouterLayout;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for manage and store ContextualStorages.
 *
 * This class is responsible for
 * - creating, and providing the ContextualStorage for a context key
 * - destroying ContextualStorages
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
abstract class AbstractContextualStorageManager<K> implements Serializable  {
    @Inject
    private BeanManager beanManager;
    private final boolean concurrent;
    private final Map<K, ContextualStorage> storageMap;

    protected AbstractContextualStorageManager(boolean concurrent) {
        if (concurrent) {
            this.storageMap = new ConcurrentHashMap<>();
        } else {
            this.storageMap = new HashMap<>();
        }
        this.concurrent = concurrent;
    }

    protected ContextualStorage getContextualStorage(K key, boolean createIfNotExist) {
        if (createIfNotExist) {
            return storageMap.computeIfAbsent(key, this::newContextualStorage);
        } else {
            return storageMap.get(key);
        }
    }

    protected ContextualStorage newContextualStorage(K key) {
        // Not required by the spec, but in reality beans are PassivationCapable.
        // Even for non serializable bean classes.
        // CDI implementations use PassivationCapable beans,
        // because injecting non serializable proxies might block serialization of
        // bean instances in a passivation capable context.
        return new ContextualStorage(beanManager, concurrent, true);
    }

    @PreDestroy
    protected void destroyAll() {
        Collection<ContextualStorage> storages = storageMap.values();
        for (ContextualStorage storage : storages) {
            AbstractContext.destroyAllActive(storage);
        }
        storageMap.clear();
    }

    protected void destroy(K key) {
        ContextualStorage storage = storageMap.remove(key);
        storage.getStorage( ).values( ).stream()
            .map(ContextualInstanceInfo::getContextualInstance)
            .filter( HasElement.class::isInstance )
            .map( HasElement.class::cast)
            .forEach( this::decouple );
        
        if (storage != null) {
            AbstractContext.destroyAllActive(storage);
        }
    }
	
    protected void decouple( HasElement willBeRemoved) {
        if(willBeRemoved instanceof Component ) {
            Component c = (Component)willBeRemoved;
            Optional<RouterLayout> maybeRouterLayout = c.getParent()
                .filter(RouterLayout.class::isInstance)
                .map(RouterLayout.class::cast);
            maybeRouterLayout.ifPresent(rl -> rl.removeRouterLayoutContent(willBeRemoved));
            if(maybeRouterLayout.isPresent())
                return;
        }
        if(willBeRemoved.getElement() != null)
            willBeRemoved.getElement().removeFromParent();
    }


    protected Set<K> getKeySet() {
        return Collections.unmodifiableSet(storageMap.keySet());
    }

}
