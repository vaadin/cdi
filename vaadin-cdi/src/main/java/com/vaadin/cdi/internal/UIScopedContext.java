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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public class UIScopedContext extends AbstractContext {

    private static final int CLEANUP_DELAY = 5000;

    private BeanManager beanManager;
    private Map<VaadinSession, Map<Contextual, ContextualStorage>> storageMap = new ConcurrentHashMap<VaadinSession, Map<Contextual, ContextualStorage>>();

    public UIScopedContext(final BeanManager beanManager) {
        super(beanManager);
        getLogger().fine("Instantiating UIScoped context");
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual,
            boolean createIfNotExist) {
        Map<Contextual, ContextualStorage> map = getStorageMapForSession();
        if (map == null) {
            return null;
        }
        // If a non-UI class has the @UIScoped annotation the contextual
        // parameter is a CDI managed bean. We need to wrap this in a UIBean so
        // that we can clean up its storage once the UI has been closed.
        if (!(contextual instanceof UIBean)
                && contextual instanceof Bean
                && !UI.class
                        .isAssignableFrom(((Bean) contextual).getBeanClass())
                && UI.getCurrent() != null) {
            contextual = new UIBean((Bean) contextual);
        }
        if (map.containsKey(contextual)) {
            return map.get(contextual);
        } else if (createIfNotExist) {
            ContextualStorage storage = new ContextualStorage(beanManager,
                    true, true);
            map.put(contextual, storage);
            return storage;
        } else {
            return null;
        }

    }

    private Map<Contextual, ContextualStorage> getStorageMapForSession() {
        VaadinSession session = VaadinSession.getCurrent();
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

    void dropSessionData(VaadinSessionDestroyEvent event) {
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

    private void dropUIData(VaadinUICloseEvent event) {
        VaadinSession session = event.getSession();
        int uiId = event.getUiId();
        getLogger().fine("Dropping UI data for UI: " + uiId);
        Map<Contextual, ContextualStorage> map = storageMap.get(session);
        if (map != null) {
            for (Contextual contextual : new HashSet<Contextual>(map.keySet())) {
                if (contextual instanceof UIBean
                        && ((UIBean) contextual).getUiId() == uiId) {
                    destroy(contextual);
                    map.remove(contextual);
                }
            }
        }
    }

    private TreeMap<Long, VaadinUICloseEvent> uiCloseQueue = new TreeMap<Long, VaadinUICloseEvent>();

    void queueUICloseEvent(VaadinUICloseEvent event) {
        // We introduce a cleanup delay because the UI gets referred to later in
        // the core cleanup process. If the UI is proxied this will cause a new
        // UI to be initialized in some CDI implementations
        // (for example Apache OpenWebBeans 1.2.1)
        long closeTime = System.currentTimeMillis() + CLEANUP_DELAY;
        while (uiCloseQueue.get(closeTime) != null)
            closeTime++;
        uiCloseQueue.put(closeTime, event);
    }

    void cleanup() {
        // Remove the UI's that have been previously queued for closing. We need
        // to protect the UI context from deletion long enough that the core
        // framework has time to do it's own cleanup.
        // We run the cleanup process from VaadinCDIServletService after the
        // results of the latest query have been sent. We do it this way to
        // avoid using a background thread and to maintain cross-implementation
        // compatibility.
        long currentTime = System.currentTimeMillis();
        SortedMap<Long, VaadinUICloseEvent> subMap = uiCloseQueue
                .headMap(currentTime);
        if (!subMap.isEmpty()) {
            Collection<Entry<Long, VaadinUICloseEvent>> entries = new ArrayList<Map.Entry<Long, VaadinUICloseEvent>>(
                    subMap.entrySet());
            for (Entry<Long, VaadinUICloseEvent> entry : entries) {
                dropUIData(entry.getValue());
                uiCloseQueue.remove(entry.getKey());
            }
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}
