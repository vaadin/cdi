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

    public VaadinContextualStorage(BeanManager beanManager,
            boolean concurrent) {
        super(beanManager, concurrent, false);
    }

    @Override
    public <T> Object getBeanKey(Contextual<T> bean) {
        if (bean instanceof UIContextual) {
            return ((UIContextual) bean).delegate;
        } else {
            return super.getBeanKey(bean);
        }
    }
}
