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

import jakarta.enterprise.context.ContextNotActiveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.cdi.util.BeanProvider;
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
    public void get_sessionExistsButNotLocked_contextNotActive() {
        SessionUnderTestContext context = new SessionUnderTestContext();
        context.activate();

        VaadinSession session = context.getSession();
        when(session.hasLock()).thenReturn(false);

        VaadinSessionScopedContext sessionContext =
                new VaadinSessionScopedContext(weld.select().select(
                        jakarta.enterprise.inject.spi.BeanManager.class).get());

        assertFalse(sessionContext.isActive());

        assertThrows(ContextNotActiveException.class, () -> {
            SessionScopedTestBean ref =
                    BeanProvider.getContextualReference(SessionScopedTestBean.class);
            ref.getState();
        });
    }

    @VaadinSessionScoped
    public static class SessionScopedTestBean extends TestBean {
    }

}
