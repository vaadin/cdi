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

import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI Extension needed to register the @CDIUI scope to the runtime.
 */
public class VaadinExtension implements Extension {

    private UIScopedContext uiScopedContext;
    private ViewScopedContext viewScopedContext;

    void afterBeanDiscovery(@Observes
    final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        uiScopedContext = new UIScopedContext(beanManager);
        afterBeanDiscovery.addContext(uiScopedContext);
        getLogger().info("UIScopedContext registered for Vaadin CDI");
        viewScopedContext = new ViewScopedContext(beanManager);
        afterBeanDiscovery.addContext(viewScopedContext);
        getLogger().info("ViewScopedContext registered for Vaadin CDI");
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinExtension.class.getCanonicalName());
    }

    private void sessionClose(@Observes VaadinSessionDestroyEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.dropSessionData(event);
        }
        if (viewScopedContext != null) {
            viewScopedContext.dropSessionData(event);
        }
    }

    private void uiClose(@Observes VaadinUICloseEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.queueUICloseEvent(event);
        }
        if (viewScopedContext != null) {
            viewScopedContext.queueUICloseEvent(event);
        }
    }

    private void requestEnd(@Observes VaadinRequestEndEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.uiCloseCleanup();
        }
        if (viewScopedContext != null) {
            viewScopedContext.uiCloseCleanup();
            viewScopedContext.clearPendingViewChange();
        }
    }

    private void navigationChanged(@Observes VaadinViewChangeEvent event) {
        if (viewScopedContext != null) {
            long sessionId = event.getSessionId();
            int uiId = event.getUiId();
            viewScopedContext.viewChangeCleanup(sessionId, uiId);
        }
    }
    
    private void navigationStarting(@Observes VaadinViewCreationEvent event) {
        if (viewScopedContext != null) {
            viewScopedContext.prepareForViewChange(event.getSessionId(),
                    event.getUIId(), event.getViewMapping());
        }
    }
}
