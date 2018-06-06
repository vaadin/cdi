/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.cdi;

import com.vaadin.cdi.InconsistentDeploymentException.ID;
import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.cdi.context.ContextWrapper;
import com.vaadin.cdi.context.UIScopedContext;
import com.vaadin.cdi.context.VaadinSessionScopedContext;
import com.vaadin.flow.component.Component;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * CDI Extension needed to register the @CDIUI scope to the runtime.
 */
public class VaadinExtension implements Extension {

    private UIScopedContext uiScopedContext;

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

        VaadinSessionScopedContext vaadinSessionScopedContext = new VaadinSessionScopedContext(beanManager);
        afterBeanDiscovery.addContext(new ContextWrapper(vaadinSessionScopedContext, VaadinSessionScoped.class));
        getLogger().info("VaadinSessionScopedContext registered for Vaadin CDI");
    }

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        uiScopedContext.init(beanManager);
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinExtension.class.getCanonicalName());
    }

}
