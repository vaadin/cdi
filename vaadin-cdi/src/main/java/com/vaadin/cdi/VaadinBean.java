package com.vaadin.cdi;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 * @author: adam-bien.com
 */
public class VaadinBean<T> {
    private Bean<T> contextual;
    private CreationalContext<T> context;
    private T beanInstance;

    public VaadinBean(Contextual<T> contextual, T beanInstance, CreationalContext<T> context) {
        this.contextual = (Bean<T>) contextual;
        this.beanInstance = beanInstance;
        this.context = context;
    }

    public Bean<T> getBean() {
        return contextual;
    }

    public T getBeanInstance() {
        return beanInstance;
    }

    public CreationalContext<T> getCreationalContext() {
        return context;
    }
}
