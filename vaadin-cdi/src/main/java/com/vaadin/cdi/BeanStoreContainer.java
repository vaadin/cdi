/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import static com.vaadin.cdi.Conventions.deriveMappingForUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
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

    private final Map<Integer, UIBeanStore> beanStores = new ConcurrentHashMap<Integer, UIBeanStore>();

    private UIBeanStore unfinishedBeanStore;

    @PostConstruct
    public void onNewSession() {
        getLogger().info("New BeanStoreContainer created");
    }

    /**
     * Creates new UI bean store for given UI. If UI is null, new empty bean
     * store will be created and returned if bean store creation is not pending.
     * If previous creation is still pending, already existing instance is
     * returned for null UI value as well.
     * 
     * 
     * @param ui
     * @return Bean store that is assigned for given UI.
     */
    public UIBeanStore getOrCreateUIBeanStoreFor(UIBean ui) {
        if (isBeanStoreCreationPending()) {
            // If creation is pending, we're instantiating bean inside
            // unfinished ui bean. That's why we want to return same bean
            // store.
            getLogger().info(
                    "Getting pending bean store " + unfinishedBeanStore);
            return unfinishedBeanStore;
        }
        int uiId = ui.getUiId();
        if (beanStores.containsKey(uiId))
            return beanStores.get(uiId);
        unfinishedBeanStore = new UIBeanStore();
        return unfinishedBeanStore;
    }

    public UIBeanStore getUIBeanStore(int uiID){
        return beanStores.get(uiID);
    }

    /**
     * @return true if UI bean store creation is pending. This means that there
     *         is unassigned UI bean store available that has not yet been
     *         assigned for any UI. Calling getOrCreateBeanStore will return
     *         this already existing but still unassigned UI bean store.
     */
    public boolean isBeanStoreCreationPending() {
        return unfinishedBeanStore != null;
    }

    /**
     * Assigns UI bean store for UI. This method should be called after the
     * whole injection hierarchy has been processed and all beans related to
     * particular UI are stored in the bean store. After assigning,
     * isBeanStoreCreationPending will return false and requesting bean store
     * for assigned ui will return the already assigned instance.
     * 
     * @param ui
     */
    public void assignPendingBeanStoreFor(UI ui,int uiUid) {
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

        if (beanStores.containsKey(uiUid)) {
            throw new IllegalArgumentException(
                    "Bean store is already assigned for another UI with path: "
                            + uiUid);
        }

        beanStores.put(uiUid, unfinishedBeanStore);
        unfinishedBeanStore = null;
    }

    @PreDestroy
    private void preDestroy() {
        getLogger().info("BeanStoreContainer is about to be destroyed: " + this);
        for (final UIBeanStore beanStore : beanStores.values()) {
            getLogger().info("Dereferencing beans from beanstore: " + beanStore);
            beanStore.dereferenceAllBeanInstances();
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(BeanStoreContainer.class.getCanonicalName());
    }
}
