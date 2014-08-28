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

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.server.VaadinSession;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public abstract class AbstractVaadinContext extends AbstractContext {

    private BeanManager beanManager;
    private Map<VaadinSession, Map<Contextual, ContextualStorage>> storageMap = new ConcurrentHashMap<VaadinSession, Map<Contextual, ContextualStorage>>();

    public AbstractVaadinContext(final BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    protected synchronized Map<Contextual, ContextualStorage> getStorageMapForSession() {
        return getStorageMapForSession(VaadinSession.getCurrent());
    }

    protected synchronized Map<Contextual, ContextualStorage> getStorageMapForSession(
            VaadinSession session) {
        if (session == null || session.isClosing()) {
            return null;
        }
        if (storageMap.containsKey(session)) {
            return storageMap.get(session);
        } else {
            Map<Contextual, ContextualStorage> map = new ConcurrentHashMap<Contextual, ContextualStorage>();
            storageMap.put(session, map);
            return map;
        }
    }

    synchronized void dropSessionData(VaadinSessionDestroyEvent event) {
        VaadinSession session = event.getSession();
        getLogger().fine("Dropping session data for session: " + session);
        Map<Contextual, ContextualStorage> map = storageMap.get(session);
        if (map != null) {
            for (Contextual contextual : new HashSet<Contextual>(map.keySet())) {
                destroy(contextual);
            }
            storageMap.remove(session);
        }
    }

    protected abstract Logger getLogger();

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
