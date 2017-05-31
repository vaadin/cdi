package com.vaadin.cdi.internal;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.ContextualStorage;

/**
 * Customized version of ContextualStorage to handle wrapper beans inherited
 * from {@link UIContextual}. ( {@link UIBean}, and {@link ViewBean} )
 *
 * We need the bean to destroy the contextual instance properly.
 * Since we cannot rely on passivation id to restore the beans,
 * they used as their own keys.
 *
 * Except for wrapper beans, because UIContextual
 * equals-hashCode designed to identify the context.
 * We use delegate bean in this case.
 *
 * @see ContextualStorage
 */
public class VaadinContextualStorage extends ContextualStorage {

    public VaadinContextualStorage(BeanManager beanManager, boolean concurrent) {
        super(beanManager, concurrent, false);
    }

    @Override
    public <T> Object getBeanKey(Contextual<T> bean) {
        if ((bean instanceof UIContextual)) {
            return ((UIContextual) bean).delegate;
        } else {
            return super.getBeanKey(bean);
        }
    }

}
