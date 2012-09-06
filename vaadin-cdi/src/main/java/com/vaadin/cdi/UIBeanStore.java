package com.vaadin.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 *
 * @author adam-bien.com
 */
/**
 * Datastructure for storing bean instances in {@link VaadinUIScoped} context.
 *
 * @author Tomi Virkki / Vaadin Ltd
 */
public class UIBeanStore {

    private final Map<Bean<?>, UIBeanStore.ContextualInstance<?>> instances = new HashMap<Bean<?>, UIBeanStore.ContextualInstance<?>>();

    public UIBeanStore() {
        System.out.println("Creating new UIBeanStore");
    }

    @SuppressWarnings("unchecked")
    protected <T> T getBeanInstance(final Bean<T> bean,
            final CreationalContext<T> creationalContext) {
        UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(bean);
        if (contextualInstance == null && creationalContext != null) {
            contextualInstance = new UIBeanStore.ContextualInstance<T>(
                    bean.create(creationalContext), creationalContext);
            instances.put(bean, contextualInstance);
        }
        return contextualInstance != null ? contextualInstance
                .getInstance() : null;
    }

    public void dereferenceAllBeanInstances() {
        for (final Bean<?> bean : new HashSet<Bean<?>>(instances.keySet())) {
            dereferenceBeanInstance(bean);
        }
    }

    public <T> void dereferenceBeanInstance(final Bean<T> bean) {
        @SuppressWarnings("unchecked")
        final UIBeanStore.ContextualInstance<T> contextualInstance = (UIBeanStore.ContextualInstance<T>) instances
                .get(bean);
        if (contextualInstance != null) {
            bean.destroy(contextualInstance.getInstance(),
                    contextualInstance.getCreationalContext());
            instances.remove(bean);
        }
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
