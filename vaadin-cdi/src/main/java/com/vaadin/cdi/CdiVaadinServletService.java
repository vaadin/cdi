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
import com.vaadin.cdi.context.VaadinSessionScopedContext;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;
import com.vaadin.flow.router.ListenerPriority;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.ServiceDestroyEvent;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SystemMessagesProvider;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Optional;

import org.apache.deltaspike.core.util.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vaadin.cdi.BeanLookup.SERVICE;

/**
 * Servlet service implementation for Vaadin CDI.
 * <p>
 * This class creates and initializes a @{@link VaadinServiceEnabled}
 * {@link Instantiator}.
 * <p>
 * Some @{@link VaadinServiceEnabled} beans can be used to customize Vaadin,
 * they are also created, and bound if found.
 * <ul>
 * <li>{@link SystemMessagesProvider} is bound to service by
 * {@link VaadinService#setSystemMessagesProvider(SystemMessagesProvider)}.
 * <li>{@link ErrorHandler} is bound to created sessions by
 * {@link VaadinSession#setErrorHandler(ErrorHandler)}.
 * </ul>
 *
 * @see CdiVaadinServlet
 */
public class CdiVaadinServletService extends VaadinServletService {

    private final CdiVaadinServiceDelegate delegate;

    public CdiVaadinServletService(CdiVaadinServlet servlet,
                                   DeploymentConfiguration configuration,
                                   BeanManager beanManager) {
        super(servlet, configuration);
        this.delegate = new CdiVaadinServiceDelegate(this, beanManager);
    }

    @Override
    public void init() throws ServiceException {
        delegate.init();
        super.init();
    }

    @Override
    public void fireUIInitListeners(UI ui) {
        delegate.addUIListeners(ui);
        super.fireUIInitListeners(ui);
    }

    public Optional<Instantiator> loadInstantiators() throws ServiceException {
        Optional<Instantiator> instantiatorOptional = delegate.lookup(Instantiator.class);
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

    public CdiVaadinServlet getServlet() {
        return (CdiVaadinServlet) super.getServlet();
    }

    /**
     * This class implements the actual instantiation and event brokering
     * functionality of {@link CdiVaadinServletService}.s
     */
    public static class CdiVaadinServiceDelegate {

        private final VaadinService vaadinService;

        private final BeanManager beanManager;

        private final UIEventListener uiEventListener;

        public CdiVaadinServiceDelegate(VaadinService vaadinService,
                BeanManager beanManager) {
            this.beanManager = beanManager;
            this.vaadinService = vaadinService;

            uiEventListener = new UIEventListener(beanManager);
        }

        public void init() throws ServiceException {
            lookup(SystemMessagesProvider.class)
                    .ifPresent(vaadinService::setSystemMessagesProvider);
            vaadinService.addUIInitListener(beanManager::fireEvent);
            vaadinService.addSessionInitListener(this::sessionInit);
            vaadinService.addSessionDestroyListener(this::sessionDestroy);
            vaadinService.addServiceDestroyListener(this::fireCdiDestroyEvent);
        }

        public void addUIListeners(UI ui) {
            ui.addAfterNavigationListener(uiEventListener);
            ui.addBeforeLeaveListener(uiEventListener);
            ui.addBeforeEnterListener(uiEventListener);
            ui.addPollListener(uiEventListener);
        }

        public <T> Optional<T> lookup(Class<T> type) throws ServiceException {
            try {
                T instance = new BeanLookup<>(beanManager, type, SERVICE).lookup();
                return Optional.ofNullable(instance);
            } catch (AmbiguousResolutionException e) {
                throw new ServiceException("There are multiple eligible CDI "
                        + type.getSimpleName() + " beans.", e);
            }
        }

        private void sessionInit(SessionInitEvent sessionInitEvent)
                throws ServiceException {
            VaadinSession session = sessionInitEvent.getSession();
            lookup(ErrorHandler.class).ifPresent(session::setErrorHandler);
            beanManager.fireEvent(sessionInitEvent);
        }

        private void sessionDestroy(SessionDestroyEvent sessionDestroyEvent) {
            beanManager.fireEvent(sessionDestroyEvent);
            if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
                // Happens on tomcat when it expires sessions upon undeploy.
                // beanManager.getPassivationCapableBean returns null for
                // passivation id,
                // so we would get an NPE from AbstractContext.destroyAllActive
                getLogger().warn("VaadinSessionScoped context does not exist. "
                        + "Maybe application is undeployed."
                        + " Can't destroy VaadinSessionScopedContext.");
                return;
            }
            getLogger().debug("VaadinSessionScopedContext destroy");
            VaadinSessionScopedContext.destroy(sessionDestroyEvent.getSession());
        }

        private void fireCdiDestroyEvent(ServiceDestroyEvent event) {
            try {
                beanManager.fireEvent(event);
            } catch (Exception e) {
                // During application shutdown on TomEE 7,
                // beans are lost at this point.
                // Does not throw an exception, but catch anything just to be sure.
                getLogger().warn("Error at destroy event distribution with CDI.",
                        e);
            }
        }

        private static Logger getLogger() {
            return LoggerFactory.getLogger(CdiVaadinServiceDelegate.class);
        }
    }

    /**
     * Static listener class, to avoid registering the whole service instance.
     */
    @ListenerPriority(-100) // navigation event listeners are last by default
    private static class UIEventListener
            implements AfterNavigationListener, BeforeEnterListener,
            BeforeLeaveListener, ComponentEventListener<PollEvent> {

        private final BeanManager beanManager;

        private UIEventListener(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        @Override
        public void afterNavigation(AfterNavigationEvent event) {
            beanManager.fireEvent(event);
        }

        @Override
        public void beforeEnter(BeforeEnterEvent event) {
            beanManager.fireEvent(event);
        }

        @Override
        public void beforeLeave(BeforeLeaveEvent event) {
            beanManager.fireEvent(event);
        }

        @Override
        public void onComponentEvent(PollEvent event) {
            beanManager.fireEvent(event);
        }
    }
}
