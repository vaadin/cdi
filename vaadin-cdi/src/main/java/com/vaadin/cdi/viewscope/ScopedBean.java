package com.vaadin.cdi.viewscope;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * 
 * @author Adam Bien, blog.adam-bien.com
 */
public class ScopedBean {
    private Object bean;
    private Contextual contextual;
    private CreationalContext creationalContext;

    public ScopedBean(Contextual contextual, CreationalContext creationalContext) {
        this.bean = contextual.create(creationalContext);
        this.contextual = contextual;
        this.creationalContext = creationalContext;
    }

    public Object getBean() {
        return bean;
    }

    public void destroy() {
        contextual.destroy(bean, creationalContext);
    }

    @Override
    public String toString() {
        return "CDIBean{" + "bean=" + bean + '}';
    }
}
