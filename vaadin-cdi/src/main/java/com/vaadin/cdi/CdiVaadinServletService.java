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

import com.vaadin.cdi.context.VaadinSessionScopedContext;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Optional;

import static com.vaadin.cdi.BeanLookup.SERVICE;

/**
 * Servlet service implementation for Vaadin CDI.
 * 
 * This class automatically initializes CDIUIProvider and provides the CDI
 * add-on events about session and request processing. For overriding this
 * class, see VaadinCDIServlet.
 */
public class CdiVaadinServletService extends VaadinServletService {

    private final BeanManager beanManager;

    public CdiVaadinServletService(CdiVaadinServlet servlet,
                                   DeploymentConfiguration configuration,
                                   BeanManager beanManager) {
        super(servlet, configuration);
        this.beanManager = beanManager;
    }


    /**
     * Static listener class,
     * to avoid registering the whole service instance.
     */
    private static class Listener
            implements SessionInitListener, SessionDestroyListener {

        private final BeanManager beanManager;

        Listener(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        @Override
        public void sessionInit(SessionInitEvent sessionInitEvent)
                throws ServiceException {
            VaadinSession session = sessionInitEvent.getSession();
            lookup(beanManager, ErrorHandler.class)
                    .ifPresent(session::setErrorHandler);
            beanManager.fireEvent(sessionInitEvent);
        }

        @Override
        public void sessionDestroy(SessionDestroyEvent sessionDestroyEvent) {
            beanManager.fireEvent(sessionDestroyEvent);
            if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
                // Happens on tomcat when it expires sessions upon undeploy.
                // beanManager.getPassivationCapableBean returns null for passivation id,
                // so we would get an NPE from AbstractContext.destroyAllActive
                getLogger().warn("VaadinSessionScoped context does not exist. " +
                        "Maybe application is undeployed." +
                        " Can't destroy VaadinSessionScopedContext.");
                return;
            }
            getLogger().debug("VaadinSessionScopedContext destroy");
            VaadinSessionScopedContext.destroy(sessionDestroyEvent.getSession());
        }

    }

    @Override
    public void init() throws ServiceException {
        lookup(beanManager, SystemMessagesProvider.class)
                .ifPresent(this::setSystemMessagesProvider);
        Listener listener = new Listener(beanManager);
        addSessionInitListener(listener);
        addSessionDestroyListener(listener);
        super.init();
    }

    @Override
    public CdiVaadinServlet getServlet() {
        return (CdiVaadinServlet) super.getServlet();
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Optional<Instantiator> instantiatorOptional =
                lookup(beanManager, Instantiator.class);
        if (instantiatorOptional.isPresent()) {
            Instantiator instantiator = instantiatorOptional.get();
            if (!instantiator.init(this)) {
                Class unproxiedClass =
                        ProxyUtils.getUnproxiedClass(instantiator.getClass());
                throw new ServiceException(
                        "Cannot init VaadinService because "
                                + unproxiedClass.getName() + " CDI bean init()"
                                + " returned false.");
            }
        } else {
            throw new ServiceException(
                    "Cannot init VaadinService "
                            + "because no CDI instantiator bean found."
            );
        }
        return instantiatorOptional;
    }

    protected static <T> Optional<T> lookup(BeanManager beanManager,
                                            Class<T> type) throws ServiceException {
        try {
            T instance = new BeanLookup<>(beanManager, type, SERVICE).get();
            return Optional.ofNullable(instance);
        } catch (AmbiguousResolutionException e) {
            throw new ServiceException(
                    "There are multiple eligible CDI " + type.getSimpleName()
                            + " beans.", e);
        }
    }

    private static org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger(CdiVaadinServletService.class
                .getCanonicalName());
    }

}
