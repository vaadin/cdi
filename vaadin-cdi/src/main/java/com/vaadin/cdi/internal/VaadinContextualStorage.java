package com.vaadin.cdi.internal;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

import org.apache.deltaspike.core.util.context.ContextualStorage;

/**
 * Customized version of ContextualStorage to also handle beans that are not
 * PassivationCapable. Such beans are used as their own keys, which is not ideal
 * but should work in most single-JVM environments.
 * 
 * @see ContextualStorage
 */
public class VaadinContextualStorage extends ContextualStorage {
    private final BeanManager beanManager;

    public VaadinContextualStorage(BeanManager beanManager, boolean concurrent) {
        super(beanManager, concurrent, true);
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
