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
 * UIScopedContext is the context for @VaadinUIScoped beans.
 */
public class UIScopedContext implements Context {

    private final BeanManager beanManager;

    public UIScopedContext(final BeanManager beanManager) {
        getLogger().info("Instantiating UIScoped context");
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUIScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        BeanStoreContainer beanStoreContainer = getSessionBoundBeanStoreContainer();

        UIBeanStore beanStore = beanStoreContainer.getOrCreateBeanStore(UI
                .getCurrent());

        T beanInstance = beanStore.getBeanInstance(contextual,
                creationalContext);

        if (isUIBean(contextual)) {
            if (beanStoreContainer.isBeanStoreCreationPending()) {
                beanStoreContainer.assignPendingBeanStoreFor((UI) beanInstance);
            }
        }

        getLogger().info("Finished getting bean " + beanInstance);
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

    /**
     * @return bean store container bound to the user's http session
     */
    private BeanStoreContainer getSessionBoundBeanStoreContainer() {
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

        BeanStoreContainer bsc = (BeanStoreContainer) beanManager.getReference(
                bean, bean.getBeanClass(),
                beanManager.createCreationalContext(bean));

        return bsc;
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}