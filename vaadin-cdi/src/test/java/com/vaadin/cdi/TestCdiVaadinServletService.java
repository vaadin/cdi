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
import javax.servlet.ServletContext;

import java.util.Properties;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCdiVaadinServletService extends CdiVaadinServletService {

    public TestCdiVaadinServletService(BeanManager beanManager,
            String servletName) {
        super(mock(CdiVaadinServlet.class), mock(DeploymentConfiguration.class),
                beanManager);
        when(getServlet().getServletName()).thenReturn(servletName);
        when(getServlet().getService()).thenReturn(this);
        when(getServlet().getServletContext())
                .thenReturn(mock(ServletContext.class));
        DeploymentConfiguration config = getDeploymentConfiguration();
        Properties properties = new Properties();
        when(config.getInitParameters()).thenReturn(properties);
    }

    @Override
    public String getMainDivId(VaadinSession session, VaadinRequest request) {
        return "test-1";
    }

    // We have nothing to do with atmosphere,
    // and mocking is much easier without it.
    @Override
    protected boolean isAtmosphereAvailable() {
        return false;
    }
}
