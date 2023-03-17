/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import javax.enterprise.inject.spi.CDI;

import java.util.Properties;

import org.mockito.Mockito;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

public class SessionUnderTestContext implements UnderTestContext {

    private VaadinSession session;
    private static ServiceUnderTestContext serviceUnderTestContext;

    private void mockSession() {
        if (serviceUnderTestContext == null) {
            serviceUnderTestContext = new ServiceUnderTestContext(
                    CDI.current().getBeanManager());
            serviceUnderTestContext.activate();
        }
        session = Mockito.mock(TestSession.class,
                Mockito.withSettings().useConstructor());
        doCallRealMethod().when(session).setAttribute(Mockito.any(String.class),
                Mockito.any());
        doCallRealMethod().when(session)
                .getAttribute(Mockito.any(String.class));
        doCallRealMethod().when(session).getService();

        when(session.getState()).thenReturn(VaadinSessionState.OPEN);

        when(session.hasLock()).thenReturn(true);
        DeploymentConfiguration configuration = Mockito
                .mock(DeploymentConfiguration.class);
        when(session.getConfiguration()).thenReturn(configuration);
        Properties props = new Properties();
        when(configuration.getInitParameters()).thenReturn(props);

        doCallRealMethod().when(session).addUI(Mockito.any());
        doCallRealMethod().when(session).getUIs();
    }

    @Override
    public void activate() {
        if (session == null) {
            mockSession();
        }
        VaadinSession.setCurrent(session);
    }

    @Override
    public void tearDownAll() {
        VaadinSession.setCurrent(null);
        if (serviceUnderTestContext != null) {
            serviceUnderTestContext.tearDownAll();
            serviceUnderTestContext = null;
        }
    }

    @Override
    public void destroy() {
        VaadinSessionScopedContext.destroy(session);
    }

    public VaadinSession getSession() {
        return session;
    }

    public static class TestSession extends VaadinSession {

        public TestSession() {
            super(serviceUnderTestContext.getService());
        }

    }
}
