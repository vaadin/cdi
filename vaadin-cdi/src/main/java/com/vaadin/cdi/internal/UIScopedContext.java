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
    protected synchronized ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        SessionData sessionData = getSessionData(createIfNotExist);
        if (sessionData == null) {
            if (createIfNotExist) {
                throw new IllegalStateException(
                        "Session data not recoverable for " + contextual);
            } else {
                // noop
                return null;
            }
        }

        StorageKey key;
        if (CurrentInstance.get(StorageKey.class) != null) {
            key = CurrentInstance.get(StorageKey.class);
        } else {
            key = new StorageKey(UI.getCurrent().getUIId());
        }

        Map<StorageKey, ContextualStorage> map = sessionData.getStorageMap();
        if (map == null) {
            return null;
        }

        if (map.containsKey(key)) {
            return map.get(key);
        } else if (createIfNotExist) {
            ContextualStorage storage = new VaadinContextualStorage(getBeanManager(),
                    true);
            map.put(key, storage);
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
