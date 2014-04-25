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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Data structure for storing bean instances in {@link CDIUI} context.
 */
public class UIBeanStore implements Serializable {

    private final Map<Contextual<?>, UIBeanStore.ContextualInstance<?>> instances = new HashMap<Contextual<?>, UIBeanStore.ContextualInstance<?>>();

    public UIBeanStore() {
        getLogger().fine("Creating new UIBeanStore " + this);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanInstance(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        getLogger().fine(
                "Getting bean instance for " + contextual + " from " + this);

        UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(contextual);

        contextualInstance = releaseUIIfInvalid(contextual, contextualInstance);

        if (contextualInstance == null && creationalContext != null) {
            contextualInstance = new UIBeanStore.ContextualInstance<T>(
                    contextual.create(creationalContext), creationalContext);
            instances.put(contextual, contextualInstance);
        }

        return contextualInstance != null ? contextualInstance.getInstance()
                : null;
    }

    /**
     * Checks if the UI (or the VaadinSession it's attached to) is closed or
     * closing and, if it is, releases the reference.
     * 
     * @param contextual
     *            the contextual
     * @param contextualInstance
     *            the contextual instance
     * @param <T>
     *            the type parameter of the contextual
     * @return The contextual instance that was passed in or null if the UI was
     *         released.
     */
    private <T> ContextualInstance<T> releaseUIIfInvalid(
            Contextual<T> contextual, ContextualInstance<T> contextualInstance) {
        if (contextualInstance != null
                && contextualInstance.getInstance() instanceof UI) {
            UI ui = (UI) contextualInstance.getInstance();
            if (ui.getSession() != null
                    && ui.getSession().getState() != VaadinSession.State.OPEN) {
                // The session is closing, clean up all attached UIs
                for (UI u : ui.getSession().getUIs()) {
                    for (Entry<Contextual<?>, ContextualInstance<?>> entry : instances
                            .entrySet()) {
                        if (entry.getValue().getInstance().equals(u)) {
                            dereferenceBeanInstance(entry.getKey());
                        }
                    }
                }
                return null;
            } else if (ui.isClosing()) {
                // only the current UI is closing and should be released
                dereferenceBeanInstance(contextual);
                return null;
            }
        }
        return contextualInstance;
    }

    /**
     * @param ui
     *            A UI instance
     * @return true if the UI or VaadinSession it's attached to is closing or
     *         closed.
     */
    private boolean isUIOrVaadinSessionClosing(UI ui) {
        return ui.isClosing()
                || (ui.getSession() != null && ui.getSession().getState() != VaadinSession.State.OPEN);
    }

    public void dereferenceAllBeanInstances() {
        for (final Contextual<?> bean : new HashSet<Contextual<?>>(
                instances.keySet())) {
            dereferenceBeanInstance(bean);
        }
    }

    public <T> void dereferenceBeanInstance(final Contextual<T> bean) {
        @SuppressWarnings("unchecked")
        final UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(bean);
        if (contextualInstance != null) {
            bean.destroy(contextualInstance.getInstance(),
                    contextualInstance.getCreationalContext());
            instances.remove(bean);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIBeanStore.class.getCanonicalName());
    }

    class ContextualInstance<T> {

        private final T instance;
        private final CreationalContext<T> creationalContext;

        public ContextualInstance(final T instance,
                final CreationalContext<T> creationalContext) {
            super();
            this.instance = instance;
            this.creationalContext = creationalContext;
        }

        public T getInstance() {
            return instance;
        }

        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
    }
}
