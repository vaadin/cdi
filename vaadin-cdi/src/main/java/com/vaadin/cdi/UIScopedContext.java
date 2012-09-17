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
public class UIScopedContext implements Context {

    private final BeanManager beanManager;

    public UIScopedContext(final BeanManager beanManager) {
        getLogger().info("Instantiating UIScoped context");
        this.beanManager = beanManager;
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        getLogger().info("Getting bean " + contextual);

        BeanStoreContainer beanStoreContainer = getBeanStoreContainer();

        UIBeanStore beanStore = beanStoreContainer.getOrCreateBeanStore(UI
                .getCurrent());

        T beanInstance = beanStore.getBeanInstance((Bean<T>) contextual,
                creationalContext);

        if (isUIBean(contextual)) {
            if (beanStore.isActivated()) {
                return beanInstance;
            } else {
                beanStoreContainer.assignUIBeanStore(beanStore,
                        (UI) beanInstance);
            }
        }

        return beanInstance;
    }

    /**
     * @param contextual
     * @return true if Vaadin UI is assignabled from given bean's representing
     *         type
     */
    private <T> boolean isUIBean(Contextual<T> contextual) {
        if (contextual instanceof Bean) {
            return UI.class.isAssignableFrom(((Bean<T>) contextual)
                    .getBeanClass());
        }

        return false;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUIScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private BeanStoreContainer getBeanStoreContainer() {
        Set<Bean<?>> beans = beanManager.getBeans(BeanStoreContainer.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "No bean store container bound for session");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean store container available for session");
        }

        Bean<?> bean = beans.iterator().next();

        return (BeanStoreContainer) beanManager.getReference(bean,
                bean.getBeanClass(), beanManager.createCreationalContext(bean));

    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}