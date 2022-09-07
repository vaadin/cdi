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

import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;

import java.util.Collections;
import java.util.List;

import org.apache.deltaspike.core.util.context.ContextualStorage;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.cdi.context.RouteScopedContext.NavigationData;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.LocationChangeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@RunWith(CdiTestRunner.class)
public class RouteContextualStorageManagerTest {

    private static final String STATE = "hello";

    private abstract static class HasElementTestBean extends TestBean
            implements HasElement {
        @Override
        public Element getElement() {
            return null;
        }
    }

    @Route("group1")
    public static class Group1 extends HasElementTestBean {
    }

    @RouteScoped
    @RouteScopeOwner(Group1.class)
    public static class MemberOfGroup1 extends HasElementTestBean {

        boolean isDestroyed;

        @PreDestroy
        private void onDestroy() {
            isDestroyed = true;
        }
    }

    @RouteScoped
    public static class NoOwnerBean extends HasElementTestBean {

        boolean isDestroyed;

        @PreDestroy
        private void onDestroy() {
            isDestroyed = true;
        }

    }

    @Route("group2")
    public static class Group2 extends HasElementTestBean {
    }

    @Route("")
    public static class InitialRoute extends HasElementTestBean {

    }

    @Route("")
    public static class ConditionalComponentRoute extends HasElementTestBean {

    }

    @RouteScoped
    @RouteScopeOwner(ConditionalComponentRoute.class)
    public static class ConditionalComponent extends HasElementTestBean {

        private void onEvent(@Observes(notifyObserver = Reception.IF_EXISTS) CustomEvent event) {
        }
    }

    private static class CustomEvent {
    }

    private UIUnderTestContext uiUnderTestContext;

    @Inject
    @RouteScopeOwner(Group1.class)
    private Provider<MemberOfGroup1> memberOfGroup1;

    @Inject
    private Provider<NoOwnerBean> noOwnerBean;

    @Inject
    private Event<BeforeEnterEvent> beforeNavigationTrigger;

    @Inject
    private Event<AfterNavigationEvent> afterNavigationTrigger;

    @Inject
    private Event<CustomEvent> customEventEventTrigger;

    private BeforeEnterEvent event;
    private AfterNavigationEvent afterEvent;
    private LocationChangeEvent changeEvent;

    private NavigationData data = Mockito.mock(NavigationData.class);

    @Inject
    private BeanManager manager;

    @Before
    public void setUp() {
        doSetUp(null, null);
    }

    @After
    public void tearDown() {
        uiUnderTestContext.tearDownAll();
    }

    @Test(expected = IllegalStateException.class)
    public void onBeforeEnter_initialNavigationTarget_scopeDoesNotExist_Throws() {
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) InitialRoute.class);
        beforeNavigationTrigger.fire(event);

        memberOfGroup1.get();
    }

    @Test(expected = IllegalStateException.class)
    public void afterNavigation_initialNavigationTarget_scopeDoesNotExist_Throws() {
        Mockito.when(afterEvent.getActiveChain())
                .thenReturn(Collections.singletonList(new InitialRoute()));
        afterNavigationTrigger.fire(afterEvent);

        memberOfGroup1.get();
    }

    @Test
    public void onBeforeEnter_group1Navigation_beansAreScoped() {
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean = memberOfGroup1.get();
        bean.setState(STATE);
        Assert.assertEquals(STATE, memberOfGroup1.get().getState());

        noOwnerBean.get().setState(STATE);
        Assert.assertEquals(STATE, noOwnerBean.get().getState());
    }

    @Test
    public void onBeforeEnter_group2NavigationAfterGroup1_beansAreDestroyed() {
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean1 = memberOfGroup1.get();
        bean1.setState(STATE);
        NoOwnerBean bean2 = noOwnerBean.get();
        bean2.setState(STATE);

        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group2.class);
        beforeNavigationTrigger.fire(event);

        Assert.assertTrue(bean1.isDestroyed);
        Assert.assertTrue(bean2.isDestroyed);

        // no owner bean is not preserved: the new one is created
        Assert.assertNotEquals(STATE, noOwnerBean.get().getState());
    }

    @Test
    public void afterNavigation_group2NavigationAftergroup1_beansAreDestroyed() {
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean1 = memberOfGroup1.get();
        bean1.setState(STATE);
        NoOwnerBean bean2 = noOwnerBean.get();
        bean2.setState(STATE);

        Mockito.when(afterEvent.getActiveChain())
                .thenReturn(Collections.singletonList(new Group2()));
        afterNavigationTrigger.fire(afterEvent);

        Assert.assertTrue(bean1.isDestroyed);
        Assert.assertTrue(bean2.isDestroyed);
    }

    @Test
    public void preserveOnRefresh_anotherUIHasSameWindowName_beanIsPreserved() {
        UI ui = doSetUp("foo", null);
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean1 = memberOfGroup1.get();
        bean1.setState(STATE);

        // set another UI instance with the same window name into the context
        doSetUp("foo", ui.getSession());
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        ComponentUtil.onComponentDetach(ui);

        Assert.assertFalse(bean1.isDestroyed);
        Assert.assertEquals(STATE, memberOfGroup1.get().getState());
    }

    @Test
    public void onBeforeEnter_anotherUIHasNoWindowName_beanIsDestroyedOnUiDestroy() {
        UI ui = doSetUp("foo", null);
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean1 = memberOfGroup1.get();
        bean1.setState(STATE);

        // set another UI instance with the same window name into the context
        doSetUp(null, ui.getSession());
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        ComponentUtil.onComponentDetach(ui);

        Assert.assertTrue(bean1.isDestroyed);
        Assert.assertNotEquals(STATE, memberOfGroup1.get().getState());
    }

    @Test
    public void getActiveContextualStorages_getAllStoragesForGivenUI() {
        RouteScopedContext context = new RouteScopedContext(manager);
        context.init(manager, null);

        UI ui = doSetUp("foo", null);

        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean = memberOfGroup1.get();

        List<ContextualStorage> storages = context
                .getActiveContextualStorages();
        Assert.assertEquals(1, storages.size());
        ContextualStorage storage = storages.get(0);

        doSetUp(null, ui.getSession());

        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) Group1.class);
        beforeNavigationTrigger.fire(event);

        MemberOfGroup1 bean1 = memberOfGroup1.get();

        storages = context.getActiveContextualStorages();
        Assert.assertEquals(1, storages.size());
        // Now the second UI is active and its storage is a different one
        Assert.assertNotSame(storage, storages.get(0));
    }

    @Test
    public void onBeforeEnter_conditionalBean_doesNotThrow() {
        Mockito.when(event.getNavigationTarget())
                .thenReturn((Class) InitialRoute.class);
        beforeNavigationTrigger.fire(event);
        customEventEventTrigger.fire(new CustomEvent());
    }

    private UI doSetUp(String windowName, VaadinSession session) {
        changeEvent = Mockito.mock(LocationChangeEvent.class);

        Mockito.when(data.getNavigationTarget())
                .thenReturn((Class) InitialRoute.class);
        event = Mockito.mock(BeforeEnterEvent.class);
        uiUnderTestContext = new UIUnderTestContext(session) {

            @Override
            public void activate() {
                super.activate();

                if (windowName != null) {
                    ExtendedClientDetails details = Mockito
                            .mock(ExtendedClientDetails.class);
                    Mockito.when(details.getWindowName())
                            .thenReturn(windowName);
                    getUi().getInternals().setExtendedClientDetails(details);
                }

                ComponentUtil.setData(getUi(), NavigationData.class, data);
                Mockito.when(changeEvent.getUI()).thenReturn(getUi());
                Mockito.when(event.getUI())
                        .thenReturn(uiUnderTestContext.getUi());
            }
        };
        uiUnderTestContext.activate();

        UI ui = uiUnderTestContext.getUi();

        uiUnderTestContext.getUi().getSession().addUI(ui);

        afterEvent = Mockito.mock(AfterNavigationEvent.class);
        Mockito.when(afterEvent.getLocationChangeEvent())
                .thenReturn(changeEvent);

        return uiUnderTestContext.getUi();
    }
}
