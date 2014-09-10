/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.cdi.internal;

import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.cdi.CDIUIProvider;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class VaadinCDIServletService extends VaadinServletService {

    private BeanManager beanManager = null;

    public final class SessionListenerImpl implements SessionInitListener,
            SessionDestroyListener {
        @Override
        public void sessionInit(SessionInitEvent event) {
            getLogger().fine("Session init");
            event.getSession().addUIProvider(new CDIUIProvider());
        }

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            getLogger().fine("Firing session destroy event.");
            VaadinSessionDestroyEvent sessionDestroyEvent = new VaadinSessionDestroyEvent(
                    CDIUtil.getSessionId(event.getSession()));
            getBeanManager().fireEvent(sessionDestroyEvent);
        }

    }

    public VaadinCDIServletService(VaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(servlet, deploymentConfiguration);

        SessionListenerImpl sessionListener = new SessionListenerImpl();
        addSessionInitListener(sessionListener);
        addSessionDestroyListener(sessionListener);
    }

    public BeanManager getBeanManager() {
        if (beanManager == null) {
            beanManager = CDIUtil.lookupBeanManager();
        }
        return beanManager;
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinCDIServletService.class
                .getCanonicalName());
    }

    @Override
    public void handleRequest(VaadinRequest request, VaadinResponse response)
            throws ServiceException {
        super.handleRequest(request, response);
        getLogger().fine("Firing cleanup event");
        getBeanManager().fireEvent(new VaadinRequestEndEvent());

    }
}
