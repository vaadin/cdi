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
        return get(contextual);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {
        UIBeanStore currentBeanStore = null;
        Bean<T> bean = (Bean<T>) contextual;

        if (UI.class.isAssignableFrom(bean.getBeanClass())) {
            UI scopedView = createScopedUI((Bean<UI>) bean,
                    (CreationalContext<UI>) creationalContext);
            currentBeanStore = getCurrentBeanStoreForUI(scopedView);
        } else {
            currentBeanStore = getCurrentBeanStore();
        }

        return currentBeanStore.getBeanInstance(bean, creationalContext);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUIScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private UI createScopedUI(Bean<UI> t, CreationalContext<UI> context) {
        return t.create(context);
    }

    private UIBeanStore getCurrentBeanStore() {
        return getCurrentBeanStoreForUI(UI.getCurrent());
    }

    private UIBeanStore getCurrentBeanStoreForUI(UI ui) {
        Set<Bean<?>> beans = beanManager.getBeans(BeanStoreContainer.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException("No UI bean store found for UI "
                    + ui);
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean store available for UI " + ui);
        }

        final Bean<?> bean = beans.iterator().next();

        final BeanStoreContainer container = (BeanStoreContainer) beanManager
                .getReference(bean, bean.getBeanClass(),
                        beanManager.createCreationalContext(bean));

        return container.getBeanStore(ui);
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}