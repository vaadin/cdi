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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */

public class ViewScopedContext extends AbstractVaadinContext {

    // When the user is injecting a bean with the @ViewScoped annotation we end
    // up having something other than a ViewBean as our contextual. We proceed
    // to give our best guess as to what the correct ViewBean is, but we then
    // need to keep a reference to it so that we the mapping remains consistent.
    // We store the relationships in this map.
    private Map<Contextual<?>, ViewBean> transientBeanMap = new HashMap<Contextual<?>, ViewBean>();

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
    protected synchronized ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        getLogger().fine("Retrieving contextual storage for " + contextual);

        Map<Contextual, ContextualStorage> map = getStorageMapForSession();
        if (map == null) {
            return null;
        }

        if (!(contextual instanceof ViewBean)) {
            if (transientBeanMap.containsKey(contextual)) {
                contextual = transientBeanMap.get(contextual);
            } else {
                if (contextual instanceof Bean) {
                    Navigator navigator = UI.getCurrent().getNavigator();
                    String viewName = null;
                    if (navigator == null) {
                        viewName = "";
                    } else {
                        String state = navigator.getState();
                        // Look for the longest viewname from a sorted list.
                        // Navigator uses the same logic to find the view to
                        // use.
                        // We cannot rely on view change events as we need to
                        // get the exact navigation state even between the pre-
                        // and postnavigation events, and we can't reliably
                        // parse the navigationstate without knowing what views
                        // have been registered.
                        for (String mapping : getViewMappings()) {
                            if (state.startsWith(mapping)) {
                                viewName = mapping;
                                break;
                            }
                        }
                    }
                    ViewBean bean = new ViewBean((Bean) contextual, viewName);
                    transientBeanMap.put(contextual, bean);
                    contextual = bean;
                } else {
                    throw new IllegalStateException(
                            "Invalid contextual get request: " + contextual);
                }
            }
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

    synchronized void dropUIData(VaadinSession session, int uiId) {
        for (Entry<Contextual<?>, ViewBean> entry : new ArrayList<Entry<Contextual<?>, ViewBean>>(
                transientBeanMap.entrySet())) {
            if (entry.getValue().uiId == uiId) {
                transientBeanMap.remove(entry.getKey());
            }
        }

        Map<Contextual, ContextualStorage> map = getStorageMapForSession(session);
        for (Entry<Contextual, ContextualStorage> entry : new ArrayList<Entry<Contextual, ContextualStorage>>(
                map.entrySet())) {
            ViewBean contextual = (ViewBean) entry.getKey();
            if (contextual.uiId == uiId) {
                map.remove(contextual);
                destroy(contextual);
            }
        }
    }

    synchronized void dropExpiredViewData(VaadinSession session, int uiId,
            String activeViewName) {
        getLogger().fine("Setting active view to " + activeViewName);

        for (Entry<Contextual<?>, ViewBean> entry : new ArrayList<Entry<Contextual<?>, ViewBean>>(
                transientBeanMap.entrySet())) {
            if (entry.getValue().uiId == uiId
                    && !entry.getValue().viewIdentifier.equals(activeViewName)) {
                transientBeanMap.remove(entry.getKey());
            }
        }

        Map<Contextual, ContextualStorage> map = getStorageMapForSession(session);
        for (Entry<Contextual, ContextualStorage> entry : new ArrayList<Entry<Contextual, ContextualStorage>>(
                map.entrySet())) {
            ViewBean contextual = (ViewBean) entry.getKey();
            if (contextual.uiId == uiId
                    && !contextual.viewIdentifier.equals(activeViewName)) {
                getLogger().fine(
                        "dropping " + contextual + " : " + entry.getValue());
                map.remove(contextual);
                destroy(contextual);
            }
        }
    }

    private List<String> getViewMappings() {
        if (viewMappings == null) {
            viewMappings = AnnotationUtil.getCDIViewMappings(getBeanManager());
        }
        return viewMappings;
    }

    @Override
    protected Logger getLogger() {
        return Logger.getLogger(ViewScopedContext.class.getCanonicalName());
    }
}
