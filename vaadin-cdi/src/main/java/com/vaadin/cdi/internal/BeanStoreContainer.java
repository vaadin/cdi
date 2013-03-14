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
 */

package com.vaadin.cdi.internal;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;

/**
 * BeanStoreContainer associates a UI instance with a UIBeanStore. There is only
 * one BeanStoreContainer per session. A BeanStoreContainer is @see @SessionScoped
 */
@SessionScoped
@SuppressWarnings("serial")
public class BeanStoreContainer implements Serializable {

    private final Map<Integer, UIBeanStore> beanStores = new ConcurrentHashMap<Integer, UIBeanStore>();

    @PostConstruct
    public void onNewSession() {
        getLogger().info("New BeanStoreContainer created");
    }

    /**
     * Creates new UI bean store for given UI. If there is no bean store for the
     * UI id, a new empty bean store will be created and returned.
     * 
     * 
     * @param ui
     * @return Bean store that is assigned for given UI.
     */
    public UIBeanStore getOrCreateUIBeanStoreFor(UIBean ui) {
        int uiId = ui.getUiId();
        if (beanStores.containsKey(uiId)) {
            return beanStores.get(uiId);
        }

        UIBeanStore beanStore = new UIBeanStore();
        beanStores.put(uiId, beanStore);
        return beanStore;
    }

    public UIBeanStore getUIBeanStore(int uiId) {
        return beanStores.get(uiId);
    }

    @PreDestroy
    private void preDestroy() {
        getLogger()
                .info("BeanStoreContainer is about to be destroyed: " + this);
        for (final UIBeanStore beanStore : beanStores.values()) {
            getLogger()
                    .info("Dereferencing beans from beanstore: " + beanStore);
            beanStore.dereferenceAllBeanInstances();
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(BeanStoreContainer.class.getCanonicalName());
    }
}
