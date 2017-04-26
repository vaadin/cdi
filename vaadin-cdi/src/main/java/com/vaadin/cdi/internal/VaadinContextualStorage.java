package com.vaadin.cdi.internal;

import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * Customized version of ContextualStorage to also handle beans that are not
 * PassivationCapable. Such beans are used as their own keys, which is not ideal
 * but should work in most single-JVM environments.
 *
 * Note:
 * Supporting non-PassivationCapable beans is theoretical.
 * Not required by the spec, but in reality beans are PassivationCapable.
 * Even for non serializable bean classes.
 *
 * CDI implementations use PassivationCapable beans,
 * because injecting non serializable proxies might block serialization of
 * bean instances in a passivation capable context.
 *
 * @see ContextualStorage
 */
public class VaadinContextualStorage extends ContextualStorage {
    private final BeanManager beanManager;

    public VaadinContextualStorage(BeanManager beanManager) {
        // Concurrency handling ignored intentionally.
        // Locking of VaadinSession is responsibility of the Vaadin Framework.
        super(beanManager, false, true);
        this.beanManager = beanManager;
    }
    
    @Override
    public <T> Object getBeanKey(Contextual<T> bean) {
        if(bean instanceof PassivationCapable) {
            return super.getBeanKey(bean);    
        } else {
            return bean;
        }
    }

    @Override
    public Contextual<?> getBean(Object beanKey) {
        if (beanKey instanceof String) {
            return beanManager.getPassivationCapableBean((String) beanKey);
        } else {
            return (Contextual<?>) beanKey;
        }
    }
}
