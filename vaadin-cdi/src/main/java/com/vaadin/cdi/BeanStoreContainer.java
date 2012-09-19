/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.ui.UI;

/**
 * BeanStoreContainer is session scoped top level UIBeanStore container that
 * hosts UI specific scopings. For each UI there will be own UI specific scope
 * and their backing instances are held within this BeanStoreContainer which is
 * singleton in one HTTP session.
 */
@SessionScoped
@SuppressWarnings("serial")
public class BeanStoreContainer implements Serializable {

    private final Map<Integer, UIBeanStore> beanStores = new HashMap<Integer, UIBeanStore>();

    private BeanManager beanManager;

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
                // If creation is not pending, we return new UIBeanStore as it
                // is UI specific.
                unfinishedBeanStore = createNewUIBeanStoreInstance();
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
     * @return New UIBeanStore instance
     */
    private UIBeanStore createNewUIBeanStoreInstance() {
        Set<Bean<?>> beans = beanManager.getBeans(UIBeanStore.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException("Could not find UIBeanStore bean");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "Ambiguous UIBeanStore reference available");
        }

        Bean<UIBeanStore> uiBeanStoreBean = (Bean<UIBeanStore>) beans
                .iterator().next();

        CreationalContext<UIBeanStore> creationalContext = beanManager
                .createCreationalContext(uiBeanStoreBean);
        return uiBeanStoreBean.create(creationalContext);
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

    public void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;

    }
}
