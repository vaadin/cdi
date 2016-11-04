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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.internal.AbstractVaadinContext.SessionData.UIData;
import com.vaadin.ui.UI;

/**
 * ViewScopedContext is the context for @ViewScoped beans.
 */
public class ViewScopedContext extends AbstractVaadinContext {

    private List<String> viewMappings;

    private class ViewStorageKey extends StorageKey {
        private final String viewName;

        public ViewStorageKey(int uiId, String viewName) {
            super(uiId);
            this.viewName = viewName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ViewStorageKey)) return false;
            if (!super.equals(o)) return false;

            ViewStorageKey that = (ViewStorageKey) o;

            return viewName != null ? viewName.equals(that.viewName) : that.viewName == null;

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (viewName != null ? viewName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "ViewStorageKey{" +
                    "uiId=" + uiId + "," +
                    "viewName='" + viewName + '\'' +
                    '}';
        }
    }


    public ViewScopedContext(final BeanManager beanManager) {
        super(beanManager);
        getLogger().fine("Instantiating ViewScoped context");
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ViewScoped.class;
    }

    @Override
    protected synchronized ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        getLogger().fine("Retrieving contextual storage for " + contextual);

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

        UI currentUI = UI.getCurrent();
        if (currentUI == null) {
            throw new IllegalStateException("Unable to resolve " + contextual + ", current UI not set.");
        }
        UIData uiData = sessionData.getUIData(currentUI.getUIId(), true);
        String viewName = uiData.getProbableInjectionPointView();
        if (viewName == null) {
            getLogger().warning("Could not determine active View");
        }

        ViewStorageKey key = new ViewStorageKey(currentUI.getUIId(), viewName);

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

    synchronized void prepareForViewChange(long sessionId, int uiId,
                                           String activeViewName) {
        getLogger().fine("Setting next view to " + activeViewName);
        SessionData sessionData = getSessionData(sessionId, true);
        UIData uiData = sessionData.getUIData(uiId, true);
        uiData.setOpeningView(activeViewName);
    }

    synchronized void viewChangeCleanup(long sessionId, int uiId) {
        getLogger().fine("ViewChangeCleanup for " + sessionId + " " + uiId);
        SessionData sessionData = getSessionData(sessionId, true);
        UIData uiData = sessionData.getUIData(uiId, true);
        if (uiData == null) {
            return;
        }

        uiData.validateTransition();
        String activeViewName = uiData.getActiveView();
        ViewStorageKey key = new ViewStorageKey(uiId, activeViewName);
        Map<StorageKey, ContextualStorage> map = sessionData.getStorageMap();
        ContextualStorage storage = map.get(key);
        getLogger().fine("dropping " + key + " : " + storage);
        map.remove(key);
        destroyAllActive(storage);
    }

    synchronized void clearPendingViewChange(long sessionId, int uiId) {
        SessionData sessionData = getSessionData(sessionId, false);
        if (sessionData != null) {
            UIData uiData = sessionData.getUIData(uiId);
            if (uiData != null) {
                uiData.clearPendingViewChange();
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return Logger.getLogger(ViewScopedContext.class.getCanonicalName());
    }
}
