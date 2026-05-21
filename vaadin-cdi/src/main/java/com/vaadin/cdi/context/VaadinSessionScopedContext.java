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

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellRegistry;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.inject.spi.BeanManager;
import com.vaadin.cdi.util.ContextUtils;
import com.vaadin.cdi.util.AbstractContext;
import com.vaadin.cdi.util.ContextualStorage;

import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.server.VaadinSession;

/**
 * Context for {@link VaadinSessionScoped @VaadinSessionScoped} beans.
 * <p>
 * Stores contextuals in {@link VaadinSession}.
 * Other Vaadin CDI contexts are stored in the corresponding {@link VaadinSessionScoped} context.
 *
 * @since 3.0
 */
public class VaadinSessionScopedContext extends AbstractContext {
    private final BeanManager beanManager;
    private static final String ATTRIBUTE_NAME = VaadinSessionScopedContext.class.getName();
    private static final AtomicBoolean STRICT_VALIDATION_INITIALIZED = new AtomicBoolean(false);
    private static boolean useStrictValidation = VaadinSessionScopeActivationPolicy.DEFAULT_STRICT;

    public VaadinSessionScopedContext(BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        VaadinSession session = VaadinSession.getCurrent();
        ContextualStorage storage = findContextualStorage(session);
        if (storage == null && createIfNotExist) {
            storage = initializeContextualStorage(beanManager, session);
        }
        return storage;
    }

    private static ContextualStorage findContextualStorage(VaadinSession session) {
        return getContextualStorage(session);
    }

    /**
     * Initializes the contextual storage for the given session. Handles locking internally.
     * @param beanManager the bean manager
     * @param session the concerned session
     * @return the initialized contextual storage
     */
    private static ContextualStorage initializeContextualStorage(final BeanManager beanManager, final VaadinSession session) {
        final ContextualStorage storage = new ContextualStorage(beanManager, false, true);
        if (session.hasLock()) {
            session.setAttribute(ATTRIBUTE_NAME, storage);
        } else {
            session.accessSynchronously(() -> session.setAttribute(ATTRIBUTE_NAME, storage));
        }
        return storage;
    }

    /**
     * Retrieves the contextual storage from the session. Handles locking internally.
     * @param session the concerned session
     * @return the contextual storage
     */
    private static ContextualStorage getContextualStorage(final VaadinSession session) {
        final AtomicReference<ContextualStorage> result = new AtomicReference<>();
        if (session.hasLock()) {
            result.set((ContextualStorage) session.getAttribute(ATTRIBUTE_NAME));
        } else {
            session.accessSynchronously(() -> result.set((ContextualStorage) session.getAttribute(ATTRIBUTE_NAME)));
        }
        return result.get();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinSessionScoped.class;
    }

    @Override
    public boolean isActive() {
        final VaadinSession session = VaadinSession.getCurrent();
        final boolean isStrict = this.shouldUseStrictChecks(session);
        if (isStrict) {
            return session != null && session.hasLock();
        }
        return session != null;
    }


    /**
     * Retrieves the VaadinSessionScopeActivationPolicy from the AppShell and Caches it.
     * @param session the current VaadinSession
     * @return true if strict validation is enabled, false otherwise.
     */
    private boolean shouldUseStrictChecks(final VaadinSession session) {
        if (STRICT_VALIDATION_INITIALIZED.get()) {
            return useStrictValidation;
        }
        if (session == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_STRICT;
        }
        final VaadinService service = session.getService();
        if (service == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_STRICT;
        }
        final VaadinContext context = service.getContext();
        if (context == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_STRICT;
        }
        final AppShellRegistry registry = AppShellRegistry.getInstance(context);
        if (registry == null) {
            return VaadinSessionScopeActivationPolicy.DEFAULT_STRICT;
        }
        if (STRICT_VALIDATION_INITIALIZED.compareAndSet(false, true)) {
            final Class<? extends AppShellConfigurator> configurator = registry.getShell();
            if (configurator.isAnnotationPresent(VaadinSessionScopeActivationPolicy.class)) {
                final VaadinSessionScopeActivationPolicy policy = configurator.getAnnotation(VaadinSessionScopeActivationPolicy.class);
                useStrictValidation = policy.strict();
            } else {
                useStrictValidation = VaadinSessionScopeActivationPolicy.DEFAULT_STRICT;
            }
        }
        return useStrictValidation;
    }

    public static void destroy(VaadinSession session) {
        ContextualStorage storage = findContextualStorage(session);
        if (storage != null) {
            AbstractContext.destroyAllActive(storage);
        }
    }

    /**
     * Guess whether this context is undeployed.
     *
     * Tomcat expires sessions after contexts are undeployed.
     * Need this guess to prevent exceptions when try to
     * properly destroy contexts on session expiration.
     *
     * @return true when context is not active, but sure it should
     */
    public static boolean guessContextIsUndeployed() {
        // Given there is a current VaadinSession, we should have an active context,
        // except we get here after the application is undeployed.
        return (VaadinSession.getCurrent() != null
                && !ContextUtils.isContextActive(VaadinSessionScoped.class));
    }

}
