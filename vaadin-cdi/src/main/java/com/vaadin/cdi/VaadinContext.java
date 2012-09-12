package com.vaadin.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.ui.UI;

/**
 * Custom CDI context for Vaadin applications. Stores references to bean
 * instances in the scope of a Vaadin UI.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public class VaadinContext implements Context {

    private final BeanManager beanManager;

    public VaadinContext(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    private UIBeanStore getCurrentBeanStore() {
        Set<Bean<?>> beans = beanManager.getBeans(BeanStoreContainer.class);
        if (beans.isEmpty()) {
            String msg = "Unable to obtain bean store for UI";
            getLogger().severe(msg);
            throw new IllegalStateException(msg);
        }
        final Bean<?> bean = beans.iterator().next();
        final BeanStoreContainer container = (BeanStoreContainer) beanManager
                .getReference(bean, bean.getBeanClass(),
                        beanManager.createCreationalContext(bean));
        UI current = UI.getCurrent();
        if (current == null) {
            throw new IllegalStateException(
                    "There is no class extending from UI");
        }
        return container.getBeanStore(current);
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {
        return getCurrentBeanStore().getBeanInstance((Bean<T>) contextual,
                creationalContext);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUIScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinContext.class.getCanonicalName());
    }
}