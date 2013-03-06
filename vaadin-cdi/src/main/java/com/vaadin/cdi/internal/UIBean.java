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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public class UIBean implements Bean {
    private Bean delegate;
    private int uiId;

    public UIBean(Bean delegate, int uiId) {
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
        if (this == o)
            return true;
        if (!(o instanceof UIBean))
            return false;

        UIBean uiBean = (UIBean) o;

        if (uiId != uiBean.uiId)
            return false;
        if (!delegate.getBeanClass().equals(uiBean.delegate.getBeanClass()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = delegate.getBeanClass().hashCode();
        result = 31 * result + uiId;
        return result;
    }
}
