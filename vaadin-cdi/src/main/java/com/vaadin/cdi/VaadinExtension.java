package com.vaadin.cdi;

import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * CDI Extension which registers VaadinContextImpl context.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public class VaadinExtension implements Extension {

    void afterBeanDiscovery(@Observes
    final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        afterBeanDiscovery.addContext(new VaadinContext(beanManager));
        getLogger().info("VaadinContext registered");
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinExtension.class.getCanonicalName());
    }

    public void registerStores(AfterBeanDiscovery abd, BeanManager bm) {
        InjectionTarget<BeanStoreContainer> beanStoreTarget = getInjectionTarget(
                BeanStoreContainer.class, bm);
        InjectionTarget<UIBeanStore> uiBeanStoreTarget = getInjectionTarget(
                UIBeanStore.class, bm);
        /*
         * abd.addBean(new CDIBean<UIBeanStore>(UIBeanStore.class,
         * uiBeanStoreTarget, Dependent.class));
         */
        abd.addBean(new CDIBean<BeanStoreContainer>(BeanStoreContainer.class,
                beanStoreTarget, SessionScoped.class));

    }

    private <T> InjectionTarget<T> getInjectionTarget(Class<T> clazz,
            BeanManager bm) {
        AnnotatedType<T> type = bm.createAnnotatedType(clazz);
        InjectionTarget<T> target = bm.createInjectionTarget(type);
        return target;
    }
}