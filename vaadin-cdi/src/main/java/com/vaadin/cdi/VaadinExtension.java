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

import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.context.ContextWrapper;
import com.vaadin.cdi.context.UIScopedContext;
import com.vaadin.cdi.context.VaadinServiceScopedContext;
import com.vaadin.cdi.context.VaadinSessionScopedContext;
import com.vaadin.flow.component.Component;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

/**
 * CDI Extension needed to register Vaadin scopes to the runtime.
 */
public class VaadinExtension implements Extension {

    private VaadinServiceScopedContext serviceScopedContext;
    private UIScopedContext uiScopedContext;

    private List<String> normalScopedComponentWarnings = new LinkedList<>();

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        serviceScopedContext.init(beanManager);
        uiScopedContext.init(beanManager);
    }

    void processManagedBean(@Observes ProcessManagedBean pmb,
            final BeanManager beanManager) {
        Bean<?> bean = pmb.getBean();
        Class<?> beanClass = bean.getBeanClass();
        Class<? extends Annotation> beanScope = bean.getScope();

        if (Component.class.isAssignableFrom(beanClass)
                && beanManager.isNormalScope(beanScope)) {
            normalScopedComponentWarnings.add("@"
                    + String.format("%-20s", beanScope.getSimpleName()) + " "
                    + beanClass.getCanonicalName());
        }

    }

    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {

        if (normalScopedComponentWarnings.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("The following Vaadin components are injected into "
                    + "normal scoped contexts:\n");
            for (String proxiedComponent : normalScopedComponentWarnings) {
                sb.append("   ");
                sb.append(proxiedComponent);
                sb.append("\n");
            }
            sb.append("This approach uses proxy objects and is "
                    + "not supported in Vaadin framework.");
            getLogger().error(sb.toString());
        }

        serviceScopedContext = new VaadinServiceScopedContext(beanManager);
        uiScopedContext = new UIScopedContext(beanManager);
        addContext(afterBeanDiscovery, serviceScopedContext, null);
        addContext(afterBeanDiscovery,
                new VaadinSessionScopedContext(beanManager), null);
        addContext(afterBeanDiscovery, uiScopedContext, NormalUIScoped.class);
    }

    private void addContext(AfterBeanDiscovery afterBeanDiscovery,
                            AbstractContext context,
                            Class<? extends Annotation> additionalScope) {
        afterBeanDiscovery.addContext(
                new ContextWrapper(context, context.getScope()));
        if (additionalScope != null) {
            afterBeanDiscovery.addContext(
                    new ContextWrapper(context, additionalScope));
        }
        getLogger().info("{} registered for Vaadin CDI",
                context.getClass().getSimpleName());
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(VaadinExtension.class.getCanonicalName());
    }

}
