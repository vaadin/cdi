/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import com.vaadin.ui.UI;

@SessionScoped
@SuppressWarnings("serial")
public class BeanStoreContainer implements Serializable {

    private final Map<Integer, UIBeanStore> beanStores = new HashMap<Integer, UIBeanStore>();

    @Inject
    UIBeanStore beanStore;

    private UIBeanStore unfinishedBeanStore;

    /**
     * Creates new bean store for given UI. If UI is null, new empty bean store
     * will be created and returned if bean store creation is not pending. If
     * creation is still pending, already existing instance is returned for null
     * ui value as well.
     * 
     * @param ui
     * @return Bean store that is assigned for given UI.
     */
    public UIBeanStore getOrCreateBeanStore(UI ui) {
        if (ui == null) {

            if (isBeanStoreCreationPending()) {
                // If creation is pending, we're instantiating bean inside
                // unfinished ui bean. That's why we want to return same bean
                // store.

                getLogger().info(
                        "Getting pending bean store " + unfinishedBeanStore);
                return unfinishedBeanStore;
            } else {
                // If creation is not pending, we return new bean store.
                //TODO: Probably wrong expectation: 
                //there is NO new BeanStore created. We only have one BeanStore per session.
                unfinishedBeanStore = beanStore;
                return unfinishedBeanStore;
            }
        } else {
            // If UI is not null, it must have assigned bean store.

            if (!beanStores.containsKey(ui.hashCode())) {
                throw new IllegalStateException("No bean store found for UI "
                        + ui);
            }

            UIBeanStore beanStore = beanStores.get(ui.hashCode());
            return beanStore;
        }
    }

    /**
     * @return true if bean store creation is pending. This means that there is
     *         unassigned bean store available that has not yet been assigned
     *         for any UI. Calling getOrCreateBeanStore will return this already
     *         existing but still unassigned bean store.
     */
    public boolean isBeanStoreCreationPending() {
        return unfinishedBeanStore != null;
    }

    /**
     * Assigns bean store for UI. This method should be called by the framework
     * after UI initialization
     * 
     * @param beanStore
     * 
     * @param ui
     */
    public void assignPendingBeanStoreFor(UI ui) {
        getLogger()
                .info("Assigning bean store " + unfinishedBeanStore
                        + " for UI " + ui);

        if (ui == null) {
            throw new IllegalArgumentException("UI cannot be null");
        }

        if (!isBeanStoreCreationPending()) {
            throw new IllegalStateException(
                    "No bean store creation is pending, unable to assign for UI");
        }

        if (beanStores.containsKey(ui.hashCode())) {
            throw new IllegalArgumentException(
                    "Bean store is already assigned for another UI");
        }

        beanStores.put(ui.hashCode(), unfinishedBeanStore);
        unfinishedBeanStore = null;
    }

    @PreDestroy
    private void preDestroy() {
        for (final UIBeanStore beanStore : beanStores.values()) {
            beanStore.dereferenceAllBeanInstances();
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(BeanStoreContainer.class.getCanonicalName());
    }
}
