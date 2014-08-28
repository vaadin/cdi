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
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public class UIScopedContext extends AbstractVaadinContext {

    private TreeMap<Long, VaadinUICloseEvent> uiCloseQueue = new TreeMap<Long, VaadinUICloseEvent>();

    private final Object cleanupLock = new Object();

    private static final int CLEANUP_DELAY = 5000;

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
        Map<Contextual, ContextualStorage> map = getStorageMapForSession();
        if (map == null) {
            return null;
        }
        // If a non-UI class has the @UIScoped annotation the contextual
        // parameter is a CDI managed bean. We need to wrap this in a UIBean so
        // that we can clean up its storage once the UI has been closed.
        if (!(contextual instanceof UIBean)
                && contextual instanceof Bean
                && !UI.class.isAssignableFrom(((Bean) contextual)
                        .getBeanClass()) && UI.getCurrent() != null) {
            contextual = new UIBean((Bean) contextual);
        }
        if (map.containsKey(contextual)) {
            return map.get(contextual);
        } else if (createIfNotExist) {
            ContextualStorage storage = new ContextualStorage(getBeanManager(),
                    true, true);
            map.put(contextual, storage);
            return storage;
        } else {
            return null;
        }

    }

    void queueUICloseEvent(VaadinUICloseEvent event) {
        synchronized (cleanupLock) {
            // We introduce a cleanup delay because the UI gets referred to
            // later in the core cleanup process. If the UI is proxied this will
            // cause a new UI to be initialized in some CDI implementations (for
            // example Apache OpenWebBeans 1.2.1)
            long closeTime = System.currentTimeMillis() + CLEANUP_DELAY;
            while (uiCloseQueue.get(closeTime) != null)
                closeTime++;
            uiCloseQueue.put(closeTime, event);
        }
    }

    private void dropUIData(VaadinSession session, int uiId) {
        getLogger().fine("Dropping UI data for UI: " + uiId);
        Map<Contextual, ContextualStorage> map = getStorageMapForSession(session);
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

    void cleanup() {
        // Remove the UI's that have been previously queued for closing. We need
        // to protect the UI context from deletion long enough that the core
        // framework has time to do it's own cleanup.
        // We run the cleanup process from VaadinCDIServletService after the
        // results of the latest query have been sent. We do it this way to
        // avoid using a background thread and to maintain cross-implementation
        // compatibility.
        synchronized (cleanupLock) {
            long currentTime = System.currentTimeMillis();
            SortedMap<Long, VaadinUICloseEvent> subMap = uiCloseQueue
                    .headMap(currentTime);
            if (!subMap.isEmpty()) {
                Collection<Entry<Long, VaadinUICloseEvent>> entries = new ArrayList<Map.Entry<Long, VaadinUICloseEvent>>(
                        subMap.entrySet());
                for (Entry<Long, VaadinUICloseEvent> entry : entries) {
                    VaadinUICloseEvent event = entry.getValue();
                    VaadinSession session = event.getSession();
                    int uiId = event.getUiId();
                    dropUIData(session, uiId);
                    uiCloseQueue.remove(entry.getKey());
                }
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}
