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

package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Manage and store ContextualStorage for view context.
 * This class is responsible for
 * - selecting the active view context
 * - creating, and providing the ContextualStorage for it
 * - destroying contextual instances
 *
 * Concurrency handling ignored intentionally.
 * Locking of VaadinSession is the responsibility of Vaadin Framework.
 */
@NormalUIScoped
public class ViewContextualStorageManager implements Serializable {
    private transient Storage openingContext;
    private Storage currentContext;
    @Inject
    private BeanManager beanManager;

    public void applyChange() {
        destroy(currentContext);
        currentContext = openingContext;
        openingContext = null;
    }

    public void prepareChange() {
        openingContext = new Storage();
    }

    public void cleanupChange() {
        destroy(openingContext);
        openingContext = null;
    }

    public ContextualStorage getContextualStorage(boolean createIfNotExist) {
        Storage storage;
        if (openingContext != null) {
            storage = openingContext;
        } else {
            storage = currentContext;
        }
        return storage.getContextualStorage(beanManager, createIfNotExist);
    }

    public boolean isActive() {
        return openingContext != null || currentContext != null;
    }

    @PreDestroy
    private void preDestroy() {
        destroy(openingContext);
        destroy(currentContext);
    }

    private void destroy(Storage storage) {
        if (storage != null) {
            storage.destroy();
        }
    }

    private static class Storage implements Serializable {
        private VaadinContextualStorage contextualStorage;

        private ContextualStorage getContextualStorage(BeanManager beanManager, boolean createIfNotExist) {
            if (createIfNotExist && contextualStorage == null) {
                contextualStorage = new VaadinContextualStorage(beanManager, false);
            }
            return contextualStorage;
        }

        private void destroy() {
            if (contextualStorage != null) {
                AbstractContext.destroyAllActive(contextualStorage);
            }
        }
    }

}
