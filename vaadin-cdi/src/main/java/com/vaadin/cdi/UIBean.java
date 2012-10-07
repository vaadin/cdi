package com.vaadin.cdi;

import com.vaadin.ui.UI;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author: adam-bien.com
 */
public class UIBean implements Bean {
    private Bean delegate;
    private int uiId;

    public UIBean(Bean delegate,int uiId) {
        this.delegate = delegate;
        this.uiId = uiId;
    }


    public int getUiId() {
        return uiId;
    }

    @Override
    public Set<Type> getTypes() {
        return delegate.getTypes();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return delegate.getQualifiers();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return delegate.getScope();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return delegate.getStereotypes();
    }

    @Override
    public Class<?> getBeanClass() {
        return delegate.getBeanClass();
    }

    @Override
    public boolean isAlternative() {
        return delegate.isAlternative();
    }

    @Override
    public boolean isNullable() {
        return delegate.isNullable();
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return delegate.getInjectionPoints();
    }

    @Override
    public Object create(CreationalContext uiCreationalContext) {
        return delegate.create(uiCreationalContext);
    }

    @Override
    public void destroy(Object components, CreationalContext uiCreationalContext) {
        delegate.destroy(components, uiCreationalContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIBean)) return false;

        UIBean uiBean = (UIBean) o;

        if (uiId != uiBean.uiId) return false;
        if (!delegate.getBeanClass().equals(uiBean.delegate.getBeanClass())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = delegate.getBeanClass().hashCode();
        result = 31 * result + uiId;
        return result;
    }
}
