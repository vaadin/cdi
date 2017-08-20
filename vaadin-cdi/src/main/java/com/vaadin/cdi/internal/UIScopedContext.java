/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public class UIScopedContext extends AbstractContext {

    private UIContextualStorageManager contextualStorageManager;

    public UIScopedContext(final BeanManager beanManager) {
        super(beanManager);
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        return contextualStorageManager.getContextualStorage(createIfNotExist);
    }

    public void init(BeanManager beanManager) {
        contextualStorageManager = BeanProvider
                .getContextualReference(beanManager, UIContextualStorageManager.class, false);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    public boolean isActive() {
        return VaadinSession.getCurrent() != null
                && contextualStorageManager != null
                && contextualStorageManager.isActive();
    }

}
