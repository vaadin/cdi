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

package com.vaadin.cdi.context;

import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.router.AfterNavigationEvent;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static javax.enterprise.event.Reception.IF_EXISTS;

/**
 * Context for {@link RouteScoped @RouteScoped} beans.
 */
public class RouteScopedContext extends AbstractContext {

    @NormalUIScoped
    public static class ContextualStorageManager
            extends AbstractContextualStorageManager<Class> {

        public ContextualStorageManager() {
            // Session lock checked in VaadinSessionScopedContext while
            // getting the session attribute.
            super(false);
        }

        private void onAfterNavigation(@Observes(notifyObserver = IF_EXISTS)
                                               AfterNavigationEvent event) {
            Set<Class> activeChain = event.getActiveChain().stream()
                    .map(Object::getClass)
                    .collect(Collectors.toSet());

            Set<Class> missingFromChain = getKeySet().stream()
                    .filter(routeCompClass -> !activeChain.contains(routeCompClass))
                    .collect(Collectors.toSet());

            missingFromChain.forEach(this::destroy);
        }

    }

    private ContextualStorageManager contextManager;
    private Supplier<Boolean> isUIContextActive;
    private BeanManager beanManager;

    public RouteScopedContext(BeanManager beanManager) {
        super(beanManager);
    }

    public void init(BeanManager beanManager,
                     Supplier<Boolean> isUIContextActive) {
        contextManager = BeanProvider
                .getContextualReference(beanManager, ContextualStorageManager.class, false);
        this.beanManager = beanManager;
        this.isUIContextActive = isUIContextActive;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return RouteScoped.class;
    }

    @Override
    public boolean isActive() {
        return isUIContextActive.get();
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual,
                                                     boolean createIfNotExist) {
        Class key = convertToKey(contextual);
        return contextManager.getContextualStorage(key, createIfNotExist);
    }

    private Class convertToKey(Contextual<?> contextual) {
        if (!(contextual instanceof Bean)) {
            if (contextual instanceof PassivationCapable) {
                final String id = ((PassivationCapable) contextual).getId();
                contextual = beanManager.getPassivationCapableBean(id);
            } else {
                throw new IllegalArgumentException(
                        contextual.getClass().getName()
                                + " is not of type " + Bean.class.getName());
            }
        }
        final Bean<?> bean = (Bean<?>) contextual;
        return bean.getQualifiers()
                .stream()
                .filter(annotation -> annotation instanceof RouteScopeOwner)
                .map(annotation -> (Class) (((RouteScopeOwner) annotation).value()))
                .findFirst()
                .orElse(bean.getBeanClass());
    }

}
