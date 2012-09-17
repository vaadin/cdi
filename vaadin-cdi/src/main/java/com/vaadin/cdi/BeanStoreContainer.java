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

    /**
     * Creates new bean store for given UI. If UI is null, new empty bean store
     * will be created and returned. To assign bean store for the UI, the bean
     * store must be activated by calling the activateBeanStore method with
     * proper UI.
     * 
     * @param ui
     * @return Bean store that is assigned for given UI.
     */
    public UIBeanStore getOrCreateBeanStore(UI ui) {
        if (ui == null) {
            getLogger().info("Instantiating new bean store");
            return beanStore.get();
        } else {
            if (!beanStores.containsKey(ui.hashCode())) {
                throw new IllegalStateException(
                        "No UI bean store found for UI " + ui);
            }

            return beanStores.get(ui.hashCode());
        }
    }

    /**
     * Assigns bean store for UI. This method should be called by the framework
     * after UI initialization
     * 
     * @param beanStore
     * 
     * @param ui
     */
    public void assignUIBeanStore(UIBeanStore beanStore, UI ui) {
        getLogger()
                .info("Assingning bean store " + beanStore + " for UI " + ui);

        if (beanStore == null) {
            throw new IllegalArgumentException("BeanStore cannot be null");
        }

        if (ui == null) {
            throw new IllegalArgumentException("UI cannot be null");
        }

        if (beanStore.isActivated()) {
            throw new IllegalStateException("Bean store is already activated");
        }

        if (beanStores.containsKey(ui.hashCode())) {
            throw new IllegalArgumentException(
                    "Bean store is already assigned for another UI");
        }

        beanStores.put(ui.hashCode(), beanStore);
        beanStore.setActivated();
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
