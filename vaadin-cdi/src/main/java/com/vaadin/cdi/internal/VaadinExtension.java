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

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.NormalViewScoped;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.internal.InconsistentDeploymentException.ID;
import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

/**
 * CDI Extension needed to register the @CDIUI scope to the runtime.
 */
public class VaadinExtension implements Extension {

    public static final class VaadinComponentProxyException extends Exception {
        public VaadinComponentProxyException(String message) {
            super(message);
        }
    }

    private UIScopedContext uiScopedContext;
    private ViewScopedContext viewScopedContext;

    private List<String> normalScopedComponentWarnings = new LinkedList<String>();

    void processManagedBean(@Observes ProcessManagedBean pmb,
            final BeanManager beanManager) {
        Bean bean = pmb.getBean();
        Class beanClass = bean.getBeanClass();
        Class beanScope = bean.getScope();

        if (Component.class.isAssignableFrom(beanClass)
                && beanManager.isNormalScope(beanScope)) {
            normalScopedComponentWarnings.add("@"
                    + String.format("%-20s", beanScope.getSimpleName()) + " "
                    + beanClass.getCanonicalName());
        }

        if (beanClass.isAnnotationPresent(CDIView.class)
                && !View.class.isAssignableFrom(beanClass)
                && !Modifier.isAbstract(beanClass.getModifiers())) {
            String message = "The non-abstract class "
                    + beanClass.getCanonicalName()
                    + " with @CDIView should implement "
                    + View.class.getCanonicalName();
            getLogger().warning(message);
            throw new InconsistentDeploymentException(ID.CDIVIEW_WITHOUT_VIEW,
                    message);
        }
    }

    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {

        if (normalScopedComponentWarnings.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("The following Vaadin components are injected into normal scoped contexts:\n");
            for (String proxiedComponent : normalScopedComponentWarnings) {
                sb.append("   ");
                sb.append(proxiedComponent);
                sb.append("\n");
            }
            sb.append("This approach uses proxy objects and has not been extensively tested with the framework. Please report any unexpected behavior. Switching to a pseudo-scoped context may also resolve potential issues.");
            getLogger().info(sb.toString());

        }

        uiScopedContext = new UIScopedContext(beanManager);
        afterBeanDiscovery.addContext(new ContextWrapper(uiScopedContext,
                UIScoped.class));
        afterBeanDiscovery.addContext(new ContextWrapper(uiScopedContext,
                NormalUIScoped.class));
        getLogger().info("UIScopedContext registered for Vaadin CDI");
        viewScopedContext = new ViewScopedContext(beanManager);
        afterBeanDiscovery.addContext(new ContextWrapper(viewScopedContext,
                ViewScoped.class));
        afterBeanDiscovery.addContext(new ContextWrapper(viewScopedContext,
                NormalViewScoped.class));
        getLogger().info("ViewScopedContext registered for Vaadin CDI");

        VaadinSessionScopedContext vaadinSessionScopedContext = new VaadinSessionScopedContext(beanManager);
        afterBeanDiscovery.addContext(vaadinSessionScopedContext);
        getLogger().info("VaadinSessionScopedContext registered for Vaadin CDI");
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

    private void requestEnd(@Observes VaadinViewChangeCleanupEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.uiCloseCleanup();
        }
        if (viewScopedContext != null) {
            viewScopedContext.uiCloseCleanup();
            viewScopedContext.clearPendingViewChange(event.getSessionId(),
                    event.getUiId());
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
