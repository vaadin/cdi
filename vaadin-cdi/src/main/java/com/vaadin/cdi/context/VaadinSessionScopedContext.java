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

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.inject.spi.BeanManager;
import org.apache.deltaspike.core.util.ContextUtils;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

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

    public VaadinSessionScopedContext(BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        VaadinSession session = VaadinSession.getCurrent();
        ContextualStorage storage = findContextualStorage(session);
        if (storage == null && createIfNotExist) {
            storage = new ContextualStorage(beanManager, false, true);
            session.setAttribute(ATTRIBUTE_NAME, storage);
        }
        return storage;
    }

    private static ContextualStorage findContextualStorage(VaadinSession session) {
        // session lock is checked inside
        return (ContextualStorage) session.getAttribute(ATTRIBUTE_NAME);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinSessionScoped.class;
    }

    @Override
    public boolean isActive() {
        return VaadinSession.getCurrent() != null;
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
