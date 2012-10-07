package com.vaadin.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;


/**
 * Datastructure for storing bean instances in {@link VaadinUI} context.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public class UIBeanStore implements Serializable {

    private final Map<Contextual<?>, UIBeanStore.ContextualInstance<?>> instances = new HashMap<Contextual<?>, UIBeanStore.ContextualInstance<?>>();

    public UIBeanStore() {
        getLogger().info("Creating new UIBeanStore " + this);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanInstance(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        getLogger().info(
                "Getting bean instance for " + contextual + " from " + this);

        UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(contextual);

        if (contextualInstance == null && creationalContext != null) {
            contextualInstance = new UIBeanStore.ContextualInstance<T>(
                    contextual.create(creationalContext), creationalContext);
            instances.put(contextual, contextualInstance);
        }

        return contextualInstance != null ? contextualInstance.getInstance()
                : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanInstance(final Bean<T> bean) {
        ContextualInstance<?> instance = instances.get(bean);
        if (instance == null) {
            return null;
        }
        return (T) instance.getInstance();
    }

    @SuppressWarnings("unchecked")
    public CreationalContext getCreationalContext(final Bean bean) {
        ContextualInstance<?> instance = instances.get(bean);
        if (instance == null) {
            return null;
        }
        return instance.getCreationalContext();
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
