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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.enterprise.context.ContextNotActiveException;

import com.vaadin.cdi.annotation.VaadinSessionScopeActivationPolicy.Policy;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.cdi.util.BeanProvider;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;

public class SessionContextTest extends AbstractContextTest<SessionContextTest.SessionScopedTestBean> {

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new SessionUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return true;
    }

    @Override
    protected Class<SessionScopedTestBean> getBeanType() {
        return SessionScopedTestBean.class;
    }

    @Test
    public void get_context_withStrictPolicy_contextNotActive() {
        try (final MockedStatic<VaadinSessionScopedContext> mockedExtension = Mockito.mockStatic(VaadinSessionScopedContext.class, Mockito.CALLS_REAL_METHODS)) {
            mockedExtension.when(VaadinSessionScopedContext::getActivationPolicy).thenReturn(Policy.STRICT);
            final SessionUnderTestContext context = new SessionUnderTestContext();
            context.activate();

            final VaadinSession session = context.getSession();
            when(session.hasLock()).thenReturn(false);

            final VaadinSessionScopedContext sessionContext =
                new VaadinSessionScopedContext(weld.select().select(
                    jakarta.enterprise.inject.spi.BeanManager.class).get());

            assertFalse(sessionContext.isActive());

            assertThrows(ContextNotActiveException.class, () -> {
                SessionScopedTestBean ref =
                    BeanProvider.getContextualReference(SessionScopedTestBean.class);
                ref.getState();
            });
        }

    }

    @Test
    public void get_context_withLenientPolicy() {
        try (final MockedStatic<VaadinSessionScopedContext> mockedExtension = Mockito.mockStatic(VaadinSessionScopedContext.class, Mockito.CALLS_REAL_METHODS)) {
            mockedExtension.when(VaadinSessionScopedContext::getActivationPolicy).thenReturn(Policy.LENIENT);
            final SessionUnderTestContext context = new SessionUnderTestContext();
            context.activate();

            final VaadinSession session = context.getSession();
            when(session.hasLock()).thenReturn(false);

            final VaadinSessionScopedContext sessionContext =
                new VaadinSessionScopedContext(weld.select().select(
                    jakarta.enterprise.inject.spi.BeanManager.class).get());

            assertTrue(sessionContext.isActive());

            assertDoesNotThrow(() -> {
                SessionScopedTestBean ref =
                    BeanProvider.getContextualReference(SessionScopedTestBean.class);
                ref.getState();
            });
        }
    }

    @ParameterizedTest(name = "CDI-Locking-Test [hasLock={0}, policy={1}]")
    @CsvSource({
        "true,  LENIENT",
        "false, LENIENT",
        "true,  STRICT"
    })
    public void get_context_withLockAndPolicy(boolean hasLock, Policy policy) {
        try (final MockedStatic<VaadinSessionScopedContext> mockedExtension =
            Mockito.mockStatic(VaadinSessionScopedContext.class, Mockito.CALLS_REAL_METHODS)) {
            mockedExtension.when(VaadinSessionScopedContext::getActivationPolicy).thenReturn(policy);
            final SessionUnderTestContext context = new SessionUnderTestContext();
            context.activate();
            final VaadinSession session = context.getSession();
            when(session.hasLock()).thenReturn(hasLock);

            final VaadinSessionScopedContext sessionContext =
                new VaadinSessionScopedContext(weld.select().select(
                    jakarta.enterprise.inject.spi.BeanManager.class).get());
            assertTrue(sessionContext.isActive());
            assertDoesNotThrow(() -> {
                SessionScopedTestBean ref =
                    BeanProvider.getContextualReference(SessionScopedTestBean.class);
                ref.getState();
            });
            if (hasLock) {
                Mockito.verify(session, Mockito.never()).accessSynchronously(Mockito.any(Command.class));
            } else {
                Mockito.verify(session, Mockito.atLeastOnce()).accessSynchronously(Mockito.any(Command.class));
            }
        }
    }


    @VaadinSessionScoped
    public static class SessionScopedTestBean extends TestBean {
    }

}
