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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.PassivationCapable;
import com.vaadin.cdi.util.BeanProvider;
import com.vaadin.cdi.util.AbstractContext;
import com.vaadin.cdi.util.ContextualStorage;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

import static jakarta.enterprise.event.Reception.IF_EXISTS;

/**
 * Context for {@link RouteScoped @RouteScoped} beans.
 */
public class RouteScopedContext extends AbstractContext {

    @VaadinSessionScoped
    public static class ContextualStorageManager
            extends AbstractContextualStorageManager<RouteStorageKey> {

        public ContextualStorageManager() {
            // Session lock checked in VaadinSessionScopedContext while
            // getting the session attribute.
            super(false);
        }

        @Override
        protected ContextualStorage newContextualStorage(RouteStorageKey key) {
            UI.getCurrent().addDetachListener(
                    event -> handleUIDetach(event.getUI(), key));
            return super.newContextualStorage(key);
        }

        private void onAfterNavigation(
                @Observes(notifyObserver = IF_EXISTS) AfterNavigationEvent event) {
            Set<Class<?>> activeChain = event.getActiveChain().stream()
                    .map(Object::getClass).collect(Collectors.toSet());

            destroyDescopedBeans(event.getLocationChangeEvent().getUI(),
                    activeChain);

        }

        private void onBeforeEnter(@Observes BeforeEnterEvent event) {
            UI ui = event.getUI();
            ComponentUtil.setData(ui, NavigationData.class, new NavigationData(
                    event.getNavigationTarget(), event.getLayouts()));

            Set<Class<?>> activeChain = new HashSet<>();
            activeChain.add(event.getNavigationTarget());
            activeChain.addAll(event.getLayouts());

            destroyDescopedBeans(ui, activeChain);
        }

        private void destroyDescopedBeans(UI ui,
                                          Set<Class<?>> navigationChain) {
            String uiStoreId = getUIStoreId(ui);

            Set<RouteStorageKey> missingKeys = getKeySet().stream()
                    .filter(key -> key.getUIId().equals(uiStoreId))
                    .filter(key -> !navigationChain.contains(key.getOwner()))
                    .collect(Collectors.toSet());

            missingKeys.forEach(this::destroy);
        }

        private void handleUIDetach(UI ui, RouteStorageKey key) {
            UI uiAfterRefresh = findPreservingUI(ui);
            if (uiAfterRefresh == null) {
                destroy(key);
            } else {
                uiAfterRefresh.addDetachListener(
                        event -> handleUIDetach(event.getUI(), key));
            }
        }

        private UI findPreservingUI(UI ui) {
            VaadinSession session = ui.getSession();
            String windowName = getWindowName(ui);
            for (UI sessionUi : session.getUIs()) {
                if (sessionUi != ui && windowName != null
                        && windowName.equals(getWindowName(sessionUi))) {
                    return sessionUi;
                }
            }
            return null;
        }

        private static String getWindowName(UI ui) {
            ExtendedClientDetails details = ui.getInternals()
                    .getExtendedClientDetails();
            if (details == null) {
                return null;
            }
            return details.getWindowName();
        }

        private RouteStorageKey getKey(UI ui, Class<?> owner) {
            ExtendedClientDetails details = ui.getInternals()
                    .getExtendedClientDetails();
            RouteStorageKey key = new RouteStorageKey(owner, getUIStoreId(ui));
            if (details == null) {
                ui.getPage().retrieveExtendedClientDetails(
                        det -> relocate(ui, key));
            }
            return key;
        }

        private void relocate(UI ui, RouteStorageKey key) {
            relocate(key,
                    new RouteStorageKey(key.getOwner(), getUIStoreId(ui)));
        }

        private List<ContextualStorage> getActiveContextualStorages() {
            return getKeySet().stream().filter(
                            key -> key.getUIId().equals(getUIStoreId(UI.getCurrent())))
                    .map(key -> getContextualStorage(key, false))
                    .collect(Collectors.toList());
        }

        private String getUIStoreId(UI ui) {
            ExtendedClientDetails details = ui.getInternals()
                    .getExtendedClientDetails();
            if (details == null) {
                return "uid-" + ui.getUIId();
            } else {
                return "win-" + getWindowName(ui);
            }
        }

    }

    private static class RouteStorageKey implements Serializable {
        private final Class<?> owner;
        private final String uiId;

        private RouteStorageKey(Class<?> owner, String uiId) {
            this.owner = owner;
            this.uiId = uiId;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RouteStorageKey)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            RouteStorageKey key = (RouteStorageKey) obj;
            return owner.equals(key.owner) && uiId.equals(key.uiId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, uiId);
        }

        @Override
        public String toString() {
            return "[ ui-key='" + getUIId() + "', owner='" + getOwner() + "' ]";
        }

        Class<?> getOwner() {
            return owner;
        }

        String getUIId() {
            return uiId;
        }

    }

    static class NavigationData {
        private final Class<?> navigationTarget;
        private final List<Class<? extends RouterLayout>> layouts;

        NavigationData(Class<?> navigationTarget,
                       List<Class<? extends RouterLayout>> layouts) {
            this.navigationTarget = navigationTarget;
            this.layouts = layouts;
        }

        Class<?> getNavigationTarget() {
            return navigationTarget;
        }

        List<Class<? extends RouterLayout>> getLayouts() {
            return layouts;
        }
    }

    private ContextualStorageManager contextManager;
    private Supplier<Boolean> isUIContextActive;
    private BeanManager beanManager;

    public RouteScopedContext(BeanManager beanManager) {
        super(beanManager);
    }

    public void init(BeanManager beanManager,
                     Supplier<Boolean> isUIContextActive) {
        contextManager = BeanProvider.getContextualReference(beanManager,
                ContextualStorageManager.class, false);
        this.beanManager = beanManager;
        this.isUIContextActive = isUIContextActive;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return RouteScoped.class;
    }

    @Override
    public boolean isActive() {
        return isUIContextActive.get();
    }

    @Override
    protected List<ContextualStorage> getActiveContextualStorages() {
        return contextManager.getActiveContextualStorages();
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual,
                                                     boolean createIfNotExist) {
        Bean<?> bean = getBean(contextual);
        UI ui = UI.getCurrent();
        Class<?> owner = getOwner(ui, bean);
        if (!navigationChainHasOwner(ui, owner) && createIfNotExist) {
            throw new IllegalStateException(String.format(
                    "Route owner '%s' instance is not available in the "
                            + "active navigation components chain: the scope defined by the bean '%s' doesn't exist.",
                    owner, bean.getBeanClass().getName()));
        }
        RouteStorageKey key = contextManager.getKey(ui, owner);
        return contextManager.getContextualStorage(key, createIfNotExist);
    }

    private boolean navigationChainHasOwner(UI ui, Class<?> owner) {
        NavigationData data = ComponentUtil.getData(ui, NavigationData.class);
        if (owner.equals(data.getNavigationTarget())) {
            return true;
        }
        return data.getLayouts().stream()
                .anyMatch(clazz -> clazz.equals(owner));
    }

    @SuppressWarnings("unchecked")
    private Class<?> getOwner(UI ui, Bean<?> bean) {
        return bean.getQualifiers().stream()
                .filter(annotation -> annotation instanceof RouteScopeOwner)
                .map(annotation -> (Class<?>) (((RouteScopeOwner) annotation)
                        .value()))
                .findFirst()
                .orElseGet(() -> getCurrentNavigationTarget(ui, bean));
    }

    @SuppressWarnings("rawtypes")
    private Class getCurrentNavigationTarget(UI ui, Bean<?> bean) {
        NavigationData data = ComponentUtil.getData(ui, NavigationData.class);
        if (data == null) {
            throw new IllegalStateException(String.format(
                    "There is no yet any navigation chain available, "
                            + "so bean '%s' has no scope and may not be injected",
                    bean.getBeanClass().getName()));
        }
        return data.getNavigationTarget();
    }

    private Bean<?> getBean(Contextual<?> contextual) {
        if (contextual instanceof Bean) {
            return (Bean<?>) contextual;
        }
        if (contextual instanceof PassivationCapable) {
            String id = ((PassivationCapable) contextual).getId();
            return beanManager.getPassivationCapableBean(id);
        } else {
            throw new IllegalArgumentException(contextual.getClass().getName()
                    + " is not of type " + Bean.class.getName());
        }
    }
}
