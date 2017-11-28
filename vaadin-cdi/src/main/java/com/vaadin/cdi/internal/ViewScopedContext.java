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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.internal.AbstractVaadinContext.SessionData.UIData;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

/**
 * ViewScopedContext is the context for @ViewScoped beans.
 */
public class ViewScopedContext extends AbstractVaadinContext {

    private List<String> viewMappings;

    public ViewScopedContext(final BeanManager beanManager) {
        super(beanManager);
        getLogger().fine("Instantiating ViewScoped context");
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ViewScoped.class;
    }

    @Override
    protected <T> Contextual<T> wrapBean(Contextual<T> bean) {
        if(!(bean instanceof UIContextual) && bean instanceof Bean && View.class.isAssignableFrom(((Bean) bean).getBeanClass())) {
            String mapping = Conventions.deriveMappingForView(((Bean) bean).getBeanClass());
            return new ViewBean((Bean) bean, mapping);
        }
        return bean;
    }

    @Override
    protected synchronized ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        getLogger().fine("Retrieving contextual storage for " + contextual);

        SessionData sessionData;
        if (contextual instanceof UIContextual) {
            sessionData = getSessionData(((UIContextual) contextual).getSessionId(),
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

        // The contextual is not a ViewContextual if we're injecting something other
        // than a CDIView with the @ViewScoped annotation. In those cases we'll
        // look up the currently active view for the current UI. Due to
        // technical limitations of the core framework this involves some
        // guesswork during view transition.
        if (!(contextual instanceof ViewContextual)) {
            UI currentUI = UI.getCurrent();
            if(currentUI == null) {
                throw new IllegalStateException("Unable to resolve " + contextual + ", current UI not set.");
            }
            UIData uiData = sessionData.getUIData(
                    currentUI.getUIId(), true);
            String viewName = uiData.getProbableInjectionPointView();
            if (viewName == null) {
                getLogger().warning("Could not determine active View");
            }
            if (contextual instanceof Bean) {
                contextual = new ViewBean((Bean) contextual, viewName);
            } else {
                contextual = new ViewContextual(contextual, viewName);
            }

        }

        Map<Contextual<?>, ContextualStorage> map = sessionData.getStorageMap();

        if (map == null) {
            return null;
        }

        if (map.containsKey(contextual)) {
            return map.get(contextual);
        } else if (createIfNotExist) {
            ContextualStorage storage = new VaadinContextualStorage(getBeanManager(),
                    true);
            map.put(contextual, storage);
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

        Map<Contextual<?>, ContextualStorage> map = sessionData.getStorageMap();
        for (Contextual<?> key : new ArrayList<Contextual<?>>(map.keySet())) {
        	if (key instanceof ViewContextual) {
        		ViewContextual contextual = (ViewContextual) key;
        	    if (contextual.uiId == uiId
     	        	&& !contextual.viewIdentifier.equals(activeViewName)) {
        	        ContextualStorage storage = map.remove(contextual);
                    destroyAllActive(storage);
        	    }
        	}
       	 }
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
