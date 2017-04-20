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

import com.vaadin.cdi.*;
import com.vaadin.cdi.internal.InconsistentDeploymentException.ID;
import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

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

        if (beanClass.isAnnotationPresent(CDIView.class)) {
            if (!View.class.isAssignableFrom(beanClass)
                    && !Modifier.isAbstract(beanClass.getModifiers())) {
                String message = "The non-abstract class "
                        + beanClass.getCanonicalName()
                        + " with @CDIView should implement "
                        + View.class.getCanonicalName();
                throwInconsistentDeployment(ID.CDIVIEW_WITHOUT_VIEW, message);
            }
            if (Dependent.class.isAssignableFrom(beanScope)) {
                String message = "The CDI View class "
                        + beanClass.getCanonicalName()
                        + " should not be Dependent.";
                throwInconsistentDeployment(ID.CDIVIEW_DEPENDENT, message);
            }
        }

    }

    private void throwInconsistentDeployment(ID errorId, String message) {
        getLogger().warning(message);
        throw new InconsistentDeploymentException(errorId, message);
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

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        viewScopedContext.init(beanManager);
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinExtension.class.getCanonicalName());
    }

    private void sessionClose(@Observes VaadinSessionDestroyEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.dropSessionData(event);
        }
    }

    private void uiClose(@Observes VaadinUICloseEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.queueUICloseEvent(event);
        }
    }

    private void requestEnd(@Observes VaadinViewChangeCleanupEvent event) {
        if (uiScopedContext != null) {
            uiScopedContext.uiCloseCleanup();
        }
    }

}
