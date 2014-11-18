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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public class UIScopedContext extends AbstractVaadinContext {

    public UIScopedContext(final BeanManager beanManager) {
        super(beanManager);
        getLogger().fine("Instantiating UIScoped context");
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    protected <T> Contextual<T> wrapBean(Contextual<T> bean) {
        if(!(bean instanceof UIContextual) && bean instanceof Bean && UI.class.isAssignableFrom(((Bean) bean).getBeanClass())) {
            return new UIBean((Bean) bean);
        }
        return bean;
    }

    @Override
    protected synchronized ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        SessionData sessionData;
        if (contextual instanceof UIContextual) {
            sessionData = getSessionData(
                    ((UIContextual) contextual).getSessionId(),
                    createIfNotExist);
        } else {
            sessionData = getSessionData(createIfNotExist);
        }
        if (sessionData == null) {
            if (createIfNotExist) {
                throw new IllegalStateException(
                        "Session data not recoverable for " + contextual);
            } else {
                // noop
                return null;
            }
        }

        // If a non-UI class has the @UIScoped annotation the contextual
        // parameter is a CDI managed bean. We need to wrap this in a
        // UIContextual so that we can clean up its storage once the UI has been
        // closed.
        if (!(contextual instanceof UIContextual)) {
            if (CurrentInstance.get(UIBean.class) != null) {
                contextual = CurrentInstance.get(UIBean.class);
            } else {
                contextual = new UIContextual(contextual);
            }
        }

        Map<Contextual<?>, ContextualStorage> map = sessionData.getStorageMap();
        if (map == null) {
            return null;
        }

        if (map.containsKey(contextual)) {
            ContextualStorage storage = map.get(contextual);
            return storage;
        } else if (createIfNotExist) {
            ContextualStorage storage = new VaadinContextualStorage(getBeanManager(),
                    true);
            map.put(contextual, storage);
            return storage;
        } else {
            return null;
        }

    }

    @Override
    protected Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}
