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

import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.AmbiguousResolutionException;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.DefaultSystemMessagesProvider;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.SystemMessagesProvider;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import static com.vaadin.cdi.SerializationUtils.serializeAndDeserialize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CdiVaadinServletServiceTest extends AbstractWeldTest {

    @VaadinServiceEnabled
    @VaadinServiceScoped
    public static class CdiSystemMessagesProvider
            implements SystemMessagesProvider {

        @Override
        public SystemMessages getSystemMessages(
                SystemMessagesInfo systemMessagesInfo) {
            return new CustomizedSystemMessages();
        }

    }

    @Singleton
    private static class UIListenerEventReceiver {

        private UI pollEventUI;

        void onPollEvent(@Observes PollEvent pollEvent) {
            pollEventUI = pollEvent.getSource();
        }
    }

    @Inject
    private BeanManager beanManager;

    private CdiVaadinServletService service;

    @AfterEach
    public void tearDown() {
        new ServiceUnderTestContext(beanManager).tearDownAll();
    }

    @Test
    public void getInstantiator_serviceInitialized_cdiInstantiatorReturned()
            throws ServiceException {
        initService(beanManager);
        final Instantiator instantiator = service.getInstantiator();
        Assertions.assertTrue(CdiInstantiator.class.isAssignableFrom(instantiator.getClass()));
    }

    @Test
    public void init_instantiatorAmbiguous_ExceptionThrown()
            throws ServiceException {
        assertAmbiguousThrowsException(InstantiatorFactory.class);
    }

    @Test
    public void init_instantiatorUnsatisfied_ExceptionThrown() {
        Assertions.assertThrows(ServiceException.class,
                () -> initServiceWithoutBeanFor(InstantiatorFactory.class));
    }

    @Test
    public void init_instantiatorInitReturnsFalse_ExceptionThrown() {
        BeanManager mockBm = mock(BeanManager.class);
        Bean<InstantiatorFactory> mockBean = mock(Bean.class);
        Set<Bean<?>> beans = Collections.singleton(mockBean);
        when(mockBm.getBeans(eq(InstantiatorFactory.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans))).thenReturn((Bean) mockBean);
        InstantiatorFactory mockInstantiatorFactory = mock(InstantiatorFactory.class);
        Context mockContext = mock(Context.class);
        when(mockBm.getContext(VaadinServiceScoped.class))
                .thenReturn(mockContext);
        when(mockContext.get(same(mockBean), any()))
                .thenReturn(mockInstantiatorFactory);
        when(mockInstantiatorFactory.createInstantitor(any())).thenReturn(null);
        Assertions.assertThrows(ServiceException.class, () -> initService(mockBm));
        verify(mockInstantiatorFactory, times(1)).createInstantitor(same(service));
    }

    @Test
    public void init_SystemMessagesProviderExists_configured() throws ServiceException {
        initService(beanManager);
        SystemMessagesProvider systemMessagesProvider =
                service.getSystemMessagesProvider();
        Assertions.assertTrue(CdiSystemMessagesProvider.class.isAssignableFrom(systemMessagesProvider.getClass()));
    }

    @Test
    public void init_SystemMessagesProviderAmbiguous_ExceptionThrown() {
        assertAmbiguousThrowsException(SystemMessagesProvider.class);
    }

    @Test
    public void init_SystemMessagesProviderMissing_defaultConfigured() {
        Assertions.assertThrows(ServiceException.class, () -> {
            initServiceWithoutBeanFor(SystemMessagesProvider.class);
            Assertions.assertTrue(DefaultSystemMessagesProvider.class.isAssignableFrom(service.getSystemMessagesProvider().getClass()));
        });
    }

    @Test
    public void loadInstantiators_serviceInitialized_instantiatorInstanceCreated()
            throws ServiceException {
        // #346
        BeanManager mockBm = mock(BeanManager.class);

        ServiceUnderTestContext serviceUnderTestContext = new ServiceUnderTestContext(
                mockBm);
        serviceUnderTestContext.activate();
        CdiVaadinServletService service = serviceUnderTestContext.getService();

        Bean<InstantiatorFactory> mockBean = mock(Bean.class);
        Set<Bean<?>> beans = Collections.singleton(mockBean);
        when(mockBm.getBeans(eq(InstantiatorFactory.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        when(mockBm.resolve(same(beans))).thenReturn((Bean) mockBean);

        CreationalContext<InstantiatorFactory> mockCreationalContext = mock(
                CreationalContext.class);
        when(mockBm.createCreationalContext(same(mockBean)))
                .thenReturn(mockCreationalContext);

        Context mockContext = mock(Context.class);
        when(mockBm.getContext(VaadinServiceScoped.class))
                .thenReturn(mockContext);

        InstantiatorFactory mockInstantiatorFactory = mock(InstantiatorFactory.class);
        when(mockContext.get(same(mockBean), same(mockCreationalContext)))
                .thenReturn(mockInstantiatorFactory);

        Instantiator mockInstantiator = mock(Instantiator.class);
        when(mockInstantiatorFactory.createInstantitor(same(service))).thenReturn(mockInstantiator);

        Optional<Instantiator> maybeInstantiator = service.loadInstantiators();
        Assertions.assertTrue(maybeInstantiator.isPresent());
        Assertions.assertEquals(mockInstantiator, maybeInstantiator.get());
    }

    @Test
    void fireUIInitListeners_serialization_UIserializableAndListenersWork() throws Exception {
        initService(beanManager);

        UIListenerEventReceiver uiListenerEventReceiver = service.getInstantiator().getOrCreate(UIListenerEventReceiver.class);
        UI ui = new UI();
        VaadinSession session = new MockVaadinSession(service);
        session.getLockInstance().lock();
        try {
            ui.getInternals().setSession(session);
            service.fireUIInitListeners(ui);
        } finally {
            session.getLockInstance().unlock();
        }

        ComponentUtil.fireEvent(ui, new PollEvent(ui, false));
        Assertions.assertEquals(ui, uiListenerEventReceiver.pollEventUI);

        UI ui2 = serializeAndDeserialize(ui);
        Assertions.assertNotNull(ui2);

        ComponentUtil.fireEvent(ui2, new PollEvent(ui2, false));
        Assertions.assertEquals(ui2, uiListenerEventReceiver.pollEventUI);
    }

    private void initService(BeanManager beanManager) throws ServiceException {
        ServiceUnderTestContext serviceUnderTestContext = new ServiceUnderTestContext(beanManager);
        serviceUnderTestContext.activate();
        service = serviceUnderTestContext.getService();
        service.init();
    }

    private void initServiceWithoutBeanFor(Class<?> type)
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(Collections.emptySet());
        initService(mockBm);
    }

    private void assertAmbiguousThrowsException(Class<?> type) {
        BeanManager mockBm = mock(BeanManager.class);
        Bean<?> mockBean = mock(Bean.class);
        Set<Bean<?>> beans = new HashSet<>(Arrays.asList(mockBean, mockBean));
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans)))
                .thenThrow(AmbiguousResolutionException.class);
        Assertions.assertThrows(ServiceException.class, () -> initService(mockBm));

        verify(mockBm, times(1)).resolve(same(beans));
    }

    private static class MockVaadinSession extends VaadinSession {

        ReentrantLock lock = new ReentrantLock();

        public MockVaadinSession(VaadinService service) {
            super(service);
        }

        @Override
        public Lock getLockInstance() {
            return lock;
        }
    }
}
