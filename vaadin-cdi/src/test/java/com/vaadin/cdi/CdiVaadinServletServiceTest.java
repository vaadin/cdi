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

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.DefaultSystemMessagesProvider;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.SystemMessagesProvider;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CdiTestRunner.class)
public class CdiVaadinServletServiceTest {

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

    @Inject
    private BeanManager beanManager;

    private CdiVaadinServletService service;

    @After
    public void tearDown() {
        new ServiceUnderTestContext(beanManager).tearDownAll();
    }

    @Test
    public void getInstantiator_serviceInitialized_cdiInstantiatorReturned()
            throws ServiceException {
        initService(beanManager);
        final Instantiator instantiator = service.getInstantiator();
        assertThat(instantiator, instanceOf(CdiInstantiator.class));
    }

    @Test(expected = ServiceException.class)
    public void init_instantiatorAmbiguous_ExceptionThrown()
            throws ServiceException {
        assertAmbiguousThrowsException(Instantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void init_instantiatorUnsatisfied_ExceptionThrown() throws ServiceException {
        initServiceWithoutBeanFor(Instantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void init_instantiatorInitReturnsFalse_ExceptionThrown()
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        Bean<Instantiator> mockBean = mock(Bean.class);
        Set<Bean<?>> beans = Collections.singleton(mockBean);
        when(mockBm.getBeans(eq(Instantiator.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans))).thenReturn((Bean) mockBean);
        Instantiator mockInstantiator = mock(Instantiator.class);
        Context mockContext = mock(Context.class);
        when(mockBm.getContext(VaadinServiceScoped.class))
                .thenReturn(mockContext);
        when(mockContext.get(same(mockBean), any()))
                .thenReturn(mockInstantiator);
        when(mockInstantiator.init(any())).thenReturn(false);
        initService(mockBm);

        verify(mockInstantiator, times(1)).init(same(service));
    }

    @Test
    public void init_SystemMessagesProviderExists_configured() throws ServiceException {
        initService(beanManager);
        SystemMessagesProvider systemMessagesProvider =
                service.getSystemMessagesProvider();
        assertThat(systemMessagesProvider,
                instanceOf(CdiSystemMessagesProvider.class));
    }

    @Test(expected = ServiceException.class)
    public void init_SystemMessagesProviderAmbiguous_ExceptionThrown()
            throws ServiceException {
        assertAmbiguousThrowsException(SystemMessagesProvider.class);
    }

    @Test(expected = ServiceException.class)
    public void init_SystemMessagesProviderMissing_defaultConfigured()
            throws ServiceException {
        initServiceWithoutBeanFor(SystemMessagesProvider.class);
        assertThat(service.getSystemMessagesProvider(),
                instanceOf(DefaultSystemMessagesProvider.class));
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

        Bean<Instantiator> mockBean = mock(Bean.class);
        Set<Bean<?>> beans = Collections.singleton(mockBean);
        when(mockBm.getBeans(eq(Instantiator.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        when(mockBm.resolve(same(beans))).thenReturn((Bean) mockBean);

        CreationalContext<Instantiator> mockCreationalContext = mock(
                CreationalContext.class);
        when(mockBm.createCreationalContext(same(mockBean)))
                .thenReturn(mockCreationalContext);

        Context mockContext = mock(Context.class);
        when(mockBm.getContext(VaadinServiceScoped.class))
                .thenReturn(mockContext);

        Instantiator mockInstantiator = mock(Instantiator.class);
        when(mockContext.get(same(mockBean), same(mockCreationalContext)))
                .thenReturn(mockInstantiator);
        when(mockInstantiator.init(same(service))).thenReturn(true);

        Optional<Instantiator> maybeInstantiator = service.loadInstantiators();
        assertTrue(maybeInstantiator.isPresent());
        assertEquals(mockInstantiator, maybeInstantiator.get());
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

    private void assertAmbiguousThrowsException(Class<?> type)
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        Bean<?> mockBean = mock(Bean.class);
        Set<Bean<?>> beans = new HashSet<>(Arrays.asList(mockBean, mockBean));
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans)))
                .thenThrow(AmbiguousResolutionException.class);
        initService(mockBm);

        verify(mockBm, times(1)).resolve(same(beans));
    }

}
