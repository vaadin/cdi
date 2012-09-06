package com.vaadin.cdi;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Scope;

import com.vaadin.ui.UI;

/**
 * CDI Extension which registers VaadinContextImpl context.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public class VaadinContext implements Extension {

    void afterBeanDiscovery(@Observes
    final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        getLogger().finest("Initializing VaadinContext CDI Extension");
        afterBeanDiscovery.addContext(new VaadinContextImpl(beanManager));
    }

    /**
     * Custom CDI context for Vaadin applications. Stores references to bean
     * instances in the scope of a Vaadin UI.
     * 
     * @author Tomi Virkki / Vaadin Ltd
     */
    private static class VaadinContextImpl implements Context {

        private final BeanManager beanManager;

        public VaadinContextImpl(final BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        private UIBeanStore getCurrentBeanStore() {
            Set<Bean<?>> beans = beanManager.getBeans(BeanStoreContainer.class);
            if (beans.isEmpty()) {
                String msg = "Unable to obtain bean store for UI";
                getLogger().warning(msg);
                throw new IllegalStateException(msg);
            }
            final Bean<?> bean = beans.iterator().next();
            final BeanStoreContainer container = (BeanStoreContainer) beanManager
                    .getReference(bean, bean.getBeanClass(),
                            beanManager.createCreationalContext(bean));
            return container.getBeanStore(UI.getCurrent());
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
    }

    /**
     * Annotation used for declaring bean class scope for VaadinUI beans
     * 
     * @author Tomi Virkki / Vaadin Ltd
     */
    @Scope
    // TODO: NormalScope
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    @Inherited
    public @interface VaadinUIScoped {
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinContext.class.getCanonicalName());
    }

}