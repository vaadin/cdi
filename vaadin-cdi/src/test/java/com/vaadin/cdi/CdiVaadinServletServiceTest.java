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

import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.ServiceException;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CdiTestRunner.class)
public class CdiVaadinServletServiceTest {

    @Inject
    private BeanManager beanManager;

    private CdiVaadinServletService service;

    @After
    public void tearDown() {
        new ServiceUnderTestContext(beanManager).tearDownAll();
    }

    @Test
    public void testInstantiatorCreated() throws ServiceException {
        initService(beanManager);
        final Instantiator instantiator = service.getInstantiator();
        assertThat(instantiator, instanceOf(CdiInstantiator.class));
    }

    @Test(expected = ServiceException.class)
    public void testAmbiguousInstantiatorThrowsException()
            throws ServiceException {
        assertAmbiguousThrowsException(Instantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void testNoInstantiatorThrowsException() throws ServiceException {
        initServiceWithoutBeanFor(Instantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void testInstantiatorInitReturnsFalseThrowsException()
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        final Bean mockBean = mock(Bean.class);
        HashSet<Bean<?>> beans = new HashSet<Bean<?>>() {{
            add(mockBean);
        }};
        when(mockBm.getBeans(eq(Instantiator.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans))).thenReturn(mockBean);
        Instantiator mockInstantiator = mock(Instantiator.class);
        when(mockBm.getReference(same(mockBean), eq(Instantiator.class), any()))
                .thenReturn(mockInstantiator);
        when(mockInstantiator.init(same(service))).thenReturn(false);
        initService(mockBm);

        verify(mockInstantiator, times(1)).init(same(service));
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
        HashSet<Bean<?>> beans = new HashSet<>();
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        initService(mockBm);
    }

    private void assertAmbiguousThrowsException(Class<?> type)
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        HashSet<Bean<?>> beans = new HashSet<Bean<?>>() {{
            add(mock(Bean.class));
            add(mock(Bean.class));
        }};
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans)))
                .thenThrow(AmbiguousResolutionException.class);
        initService(mockBm);

        verify(mockBm, times(1)).resolve(same(beans));
    }

}
