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

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.ui.UI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;

/**
 * Container for {@link BeanStore}s that are associated with a {@link UI}. There
 * is only one {@code UIBeanStoreContainer} per session.
 */
@SessionScoped
class UIBeanStoreContainer implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(UIBeanStoreContainer.class.getCanonicalName());
    private final Map<Integer, BeanStore> beanStores = new ConcurrentHashMap<Integer, BeanStore>();
    private BeanStore unfinishedBeanStore;

    @PostConstruct
    void postConstruct() {
        logger.log(Level.INFO, "New UIBeanStoreContainer created: {0}", this);
    }

    /**
     * Returns the {@link BeanStore} for the given UI. If there is a pending
     * bean store, it will always be returned. Otherwise, if the UI is null or
     * has not been registered with the container yet, a new empty bean store
     * will be created and returned.
     *
     * @see #isBeanStoreCreationPending()
     */
    BeanStore getOrCreateUIBeanStoreFor(VaadinUIBean ui) {
        if (isBeanStoreCreationPending()) {
            logger.log(Level.INFO, "Returning pending BeanStore {0} for bean {1}", new Object[]{unfinishedBeanStore, ui});
            return unfinishedBeanStore;
        }
        final int uiId = (ui == null ? -1 : ui.getUiId());
        if (beanStores.containsKey(uiId)) {
            return beanStores.get(uiId);
        }
        logger.log(Level.INFO, "Creating new BeanStore for bean {0}", ui);
        unfinishedBeanStore = new BeanStore();
        return unfinishedBeanStore;
    }

    /**
     * Returns whether bean store creation is pending. This means that there is
     * a bean store instance that has not yet been assigned to a UI.
     *
     * @see #getOrCreateUIBeanStoreFor(com.vaadin.cdi.internal.VaadinUIBean)
     */
    boolean isBeanStoreCreationPending() {
        return unfinishedBeanStore != null;
    }

    /**
     * Assigns the pending bean store to the specified UI. This method should be
     * called after the whole injection hierarchy has been processed and all
     * beans related to the particular UI are stored in the bean store. After
     * this method has been invoked, {@link #isBeanStoreCreationPending() } will
     * return false and the bean store will have been registered with the
     * container.
     */
    void assignPendingBeanStoreFor(UI ui, int uiId) {
        if (ui == null) {
            throw new IllegalArgumentException("UI cannot be null");
        }

        if (!isBeanStoreCreationPending()) {
            throw new IllegalStateException(
                    "No bean store creation is pending, unable to assign for UI");
        }

        if (beanStores.containsKey(uiId)) {
            throw new IllegalArgumentException(
                    "Bean store is already assigned for another UI with ID "
                    + uiId);
        }
        logger.log(Level.INFO, "Assigning BeanStore {0} to UI {1}", new Object[]{unfinishedBeanStore, ui});
        ui.addDetachListener(createUiDetachListener(ui, uiId));
        beanStores.put(uiId, unfinishedBeanStore);
        unfinishedBeanStore = null;

    }

    private ClientConnector.DetachListener createUiDetachListener(final UI ui, final int uiId) {
        return new ClientConnector.DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                logger.log(Level.INFO, "UI {0} has been detached from the application", ui);
                ui.removeDetachListener(this);
                BeanStore beanStore = beanStores.remove(uiId);
                if (beanStore != null) {
                    logger.log(Level.INFO, "Destroying beans in BeanStore {0} and removing it from UIBeanStoreContainer {1}", new Object[]{beanStore, UIBeanStoreContainer.this});
                    beanStore.destroyAllBeanInstances();
                }
            }
        };
    }

    @PreDestroy
    void preDestroy() {
        logger.log(Level.INFO, "UIBeanStoreContainer {0} is about to be destroyed", this);
        for (final BeanStore beanStore : beanStores.values()) {
            logger.log(Level.INFO, "Destroying beans in BeanStore {0}", beanStore);
            beanStore.destroyAllBeanInstances();
        }
    }
}
