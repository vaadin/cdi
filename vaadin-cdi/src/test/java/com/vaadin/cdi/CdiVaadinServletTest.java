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

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Collections;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.di.ResourceProvider;
import com.vaadin.flow.server.VaadinServletService;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class CdiVaadinServletTest {

    @Inject
    private BeanManager beanManager;

    private CdiVaadinServlet servlet;

    @Before
    public void setUp() throws ServletException, IOException {
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        final ServletContext servletContext = Mockito
                .mock(ServletContext.class);
        ClassLoader loader = CdiVaadinServletServiceTest.class.getClassLoader();
        Mockito.when(servletContext.getClassLoader()).thenReturn(loader);

        Mockito.when(servletConfig.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());
        Mockito.when(servletConfig.getServletContext())
                .thenReturn(servletContext);
        Mockito.when(servletConfig.getServletName()).thenReturn("test");
        Mockito.when(servletContext.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());
        servlet = new CdiVaadinServlet();
        BeanProvider.injectFields(servlet);

        Lookup lookup = Mockito.mock(Lookup.class);
        Mockito.when(servletContext.getAttribute(Lookup.class.getName()))
                .thenReturn(lookup);
        ResourceProvider provider = Mockito.mock(ResourceProvider.class);
        Mockito.doAnswer(invocation -> {
            return Collections.singletonList(
                    CdiVaadinServletTest.class.getClassLoader().getResource(
                            invocation.getArgumentAt(1, String.class)));
        }).when(provider).getApplicationResources(Mockito.any(), Mockito.any());
        Mockito.when(lookup.lookup(ResourceProvider.class))
                .thenReturn(provider);

        servlet.init(servletConfig);
    }

    @After
    public void tearDown() {
        new ServiceUnderTestContext(beanManager).tearDownAll();
        servlet.destroy();
    }

    @Test
    public void getService_servletInitialized_cdiVaadinServletServiceReturned() {
        final VaadinServletService service = servlet.getService();
        assertThat(service, instanceOf(CdiVaadinServletService.class));
    }
}
