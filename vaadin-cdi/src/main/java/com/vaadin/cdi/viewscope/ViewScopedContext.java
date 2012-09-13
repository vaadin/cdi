package com.vaadin.cdi.viewscope;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 * Maintains view-scoped managed beans
 * 
 * @author Adam Bien, adam-bien.com
 */
public class ViewScopedContext implements Context {

    private ConcurrentHashMap<String, ScopedBean> context = null;
    private boolean active;

    private ViewScopedContext() {
        this.context = new ConcurrentHashMap<String, ScopedBean>();
        this.active = true;
    }

    @Override
    public <T> T get(Contextual<T> contextual,
            CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        final String beanName = bean.getName();
        T foundBean = get(contextual);
        if (foundBean != null) {
            return foundBean;
        } else {
            final ScopedBean cdiBean = new ScopedBean(contextual,
                    creationalContext);
            this.context.put(beanName, cdiBean);
            return (T) cdiBean.getBean();
        }
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        final String beanName = ((Bean) contextual).getName();
        final ScopedBean cdiBean = context.get(beanName);
        if (cdiBean == null)
            return null;
        return (T) cdiBean.getBean();
    }

    public void shutdown() {
        for (ScopedBean contextual : context.values()) {
            contextual.destroy();
        }
        this.context.clear();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinViewScoped.class;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
