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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.ui.UI;

@SessionScoped
@SuppressWarnings("serial")
public class BeanStoreContainer implements Serializable {

    private final Map<Integer, UIBeanStore> beanStores = new HashMap<Integer, UIBeanStore>();

    @Inject
    Instance<UIBeanStore> beanStore;

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
                getLogger().info(
                        "Getting bean store with creation pending "
                                + unfinishedBeanStore);
                return unfinishedBeanStore;
            } else {
                unfinishedBeanStore = beanStore.get();
                getLogger().info(
                        "Instantiating new bean store " + unfinishedBeanStore);
                return unfinishedBeanStore;
            }
        } else {
            if (!beanStores.containsKey(ui.hashCode())) {
                throw new IllegalStateException(
                        "No UI bean store found for UI " + ui);
            }

            UIBeanStore beanStore = beanStores.get(ui.hashCode());
            getLogger().info("Getting bean store " + beanStore);
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
                .info("Assingning bean store " + beanStore + " for UI " + ui);

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
