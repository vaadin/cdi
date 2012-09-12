/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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

    public UIBeanStore getBeanStore(final UI current) {
        if (current == null) {
            throw new IllegalArgumentException("UI cannot be null");
        }
        final Integer key = current != null ? current.hashCode() : null;
        if (!beanStores.containsKey(key)) {
            beanStores.put(key, this.beanStore.get());
        }
        return beanStores.get(key);
    }

    public void uiInitialized(final UI ui) {
        beanStores.put(ui.hashCode(), beanStores.remove(null));
        // TODO: Listen for Ui close -> Dereference beans of the related
        // beanstore
    }

    @PreDestroy
    private void preDestroy() {
        for (final UIBeanStore beanStore : beanStores.values()) {
            beanStore.dereferenceAllBeanInstances();
        }
    }
}
