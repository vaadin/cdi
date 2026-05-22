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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.cdi.DeploymentValidator.BeanInfo;
import com.vaadin.cdi.annotation.NormalRouteScoped;
import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy;
import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy.Policy;
import com.vaadin.cdi.context.ContextWrapper;
import com.vaadin.cdi.context.RouteScopedContext;
import com.vaadin.cdi.context.UIScopedContext;
import com.vaadin.cdi.context.VaadinServiceScopedContext;
import com.vaadin.cdi.context.VaadinSessionScopedContext;
import com.vaadin.cdi.util.AbstractContext;
import com.vaadin.cdi.util.BeanProvider;
import com.vaadin.cdi.util.DependentProvider;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellRegistry;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;

/**
 * CDI Extension needed to register Vaadin scopes to the runtime.
 */
public class VaadinExtension implements Extension {

    private VaadinServiceScopedContext serviceScopedContext;
    private UIScopedContext uiScopedContext;
    private RouteScopedContext routeScopedContext;
    private Set<BeanInfo> beanInfoSet = new HashSet<>();

    /**
     * The current VaadinSessionScopeActivationPolicy.
     */
    private static Policy vaadinSessionScopeActivationPolicy = VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;

    private void storeBeanValidationInfo(@Observes ProcessBean processBean) {
        beanInfoSet.add(new BeanInfo(processBean.getBean(),
                processBean.getAnnotated()));
    }

    private void addContexts(@Observes AfterBeanDiscovery afterBeanDiscovery,
            BeanManager beanManager) {
        serviceScopedContext = new VaadinServiceScopedContext(beanManager);
        afterBeanDiscovery.<ServiceInitEvent>addObserverMethod()
            .observedType(ServiceInitEvent.class)
            .notifyWith(instance -> this.onServiceInit(instance.getEvent()));
        uiScopedContext = new UIScopedContext(beanManager);
        routeScopedContext = new RouteScopedContext(beanManager);
        addContext(afterBeanDiscovery, serviceScopedContext, null);
        addContext(afterBeanDiscovery,
                new VaadinSessionScopedContext(beanManager), null);
        addContext(afterBeanDiscovery, uiScopedContext, NormalUIScoped.class);
        addContext(afterBeanDiscovery, routeScopedContext,
                NormalRouteScoped.class);
    }

    /**
     * Called when the VaadinService is initialized.
     * @param event the ServiceInitEvent
     */
    private void onServiceInit(final ServiceInitEvent event) {
        vaadinSessionScopeActivationPolicy = this.determineVaadinSessionScopeActivationPolicy(event.getSource());
    }

    /**
     * Determine the VaadinSessionScopeActivationPolicy for the current VaadinService.
     * @param vaadinService the current VaadinService
     * @return the determined policy
     */
    private Policy determineVaadinSessionScopeActivationPolicy(final VaadinService vaadinService) {
        if (vaadinService == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
        }
        final VaadinContext context = vaadinService.getContext();
        if (context == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
        }
        final AppShellRegistry registry = AppShellRegistry.getInstance(context);
        if (registry == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
        }
        final Class<? extends AppShellConfigurator> configurator = registry.getShell();
        if (configurator == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
        }
        if (configurator.isAnnotationPresent(VaadinSessionScopeActivationPolicy.class)) {
            return configurator.getAnnotation(VaadinSessionScopeActivationPolicy.class).value();
        }
        return VaadinSessionScopeActivationPolicy.DEFAULT_POLICY;
    }

    // Validate annotated executor

    private void initializeContexts(@Observes AfterDeploymentValidation adv,
            BeanManager beanManager) {
        serviceScopedContext.init(beanManager);
        uiScopedContext.init(beanManager);
        routeScopedContext.init(beanManager, uiScopedContext::isActive);
    }

    private void validateDeployment(@Observes AfterDeploymentValidation adv,
            BeanManager beanManager) {
        DependentProvider<DeploymentValidator> validatorProvider = BeanProvider
                .getDependent(beanManager, DeploymentValidator.class);
        DeploymentValidator validator = validatorProvider.get();
        validator.validate(beanInfoSet, adv::addDeploymentProblem);
        validatorProvider.destroy();
        beanInfoSet = null;
    }

    private void ensureAtMostOneVaadinTaskExecutor(
            @Observes AfterDeploymentValidation event,
            BeanManager beanManager) {
        Set<Bean<?>> candidates = beanManager.getBeans(Executor.class,
                BeanLookup.SERVICE);
        if (candidates.size() > 1) {
            event.addDeploymentProblem(new IllegalStateException(
                    "There must be at most one Executor bean annotated with @"
                            + VaadinServiceEnabled.class.getSimpleName()
                            + " in the application. " + "Found "
                            + candidates.size() + ": " + candidates));
        }
    }

    private void addContext(AfterBeanDiscovery afterBeanDiscovery,
            AbstractContext context,
            Class<? extends Annotation> additionalScope) {
        afterBeanDiscovery
                .addContext(new ContextWrapper(context, context.getScope()));
        if (additionalScope != null) {
            afterBeanDiscovery
                    .addContext(new ContextWrapper(context, additionalScope));
        }
        getLogger().info("{} registered for Vaadin CDI",
                context.getClass().getSimpleName());
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(VaadinExtension.class);
    }

    /**
     * Get the current VaadinSessionScopeActivationPolicy.
     * @return the current VaadinSessionScopeActivationPolicy
     */
    public static Policy getVaadinSessionScopeActivationPolicy() {
        return vaadinSessionScopeActivationPolicy;
    }

}
