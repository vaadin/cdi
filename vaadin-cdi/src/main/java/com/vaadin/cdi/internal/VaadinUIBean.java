/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.cdi.internal;

import com.vaadin.ui.UI;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * TODO Document me!
 */
class VaadinUIBean<T extends UI> implements Bean<T>, PassivationCapable, java.io.Serializable {

    private static final Logger logger = Logger.getLogger(VaadinUIBean.class.getCanonicalName());
    private final Bean<T> delegate;
    private final int uiId;
    private final String passivationId;

    VaadinUIBean(Bean<T> delegate, int uiId) {
        assert delegate != null : "delegate must not be null";
        if (!UI.class.isAssignableFrom(delegate.getBeanClass())) {
            throw new IllegalArgumentException("The delegate bean is not a UI");
        }
        if (!(delegate instanceof PassivationCapable)) {
            logger.log(Level.WARNING, "Bean delegate {0} is not passivation capable", delegate);
        }
        if (!(delegate instanceof java.io.Serializable)) {
            logger.log(Level.WARNING, "Bean delegate {0} is not serializable", delegate);
        }

        this.delegate = delegate;
        this.uiId = uiId;
        passivationId = getClass().getCanonicalName() + ":" + uiId;
    }

    int getUiId() {
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
    public T create(CreationalContext<T> creationalContext) {
        logger.log(Level.FINER, "Creating new bean instance of bean {0} using creational context {1}",
                new Object[]{this, creationalContext});
        return delegate.create(creationalContext);
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        logger.log(Level.FINER, "Destroying bean instance {0} of bean {1} using creational context {2}",
                new Object[]{instance, this, creationalContext});
        delegate.destroy(instance, creationalContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VaadinUIBean)) {
            return false;
        }

        VaadinUIBean uiBean = (VaadinUIBean) o;

        if (uiId != uiBean.uiId) {
            return false;
        }
        if (!delegate.getBeanClass().equals(uiBean.delegate.getBeanClass())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = delegate.getBeanClass().hashCode();
        result = 31 * result + uiId;
        return result;
    }

    @Override
    public String getId() {
        if (delegate instanceof PassivationCapable) {
            return ((PassivationCapable) delegate).getId();
        } else {
            return passivationId;
        }
    }
}
