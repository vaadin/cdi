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

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(CdiTestRunner.class)
public class RouteContextualStorageManagerTest {

    private static final String STATE = "hello";

    private abstract static class HasElementTestBean extends TestBean implements HasElement {
        @Override
        public Element getElement() {
            return null;
        }
    }

    @RouteScoped
    @Route("group1")
    public static class Group1 extends HasElementTestBean  {
    }

    @RouteScoped
    @RouteScopeOwner(Group1.class)
    public static class MemberOfGroup1 extends HasElementTestBean {
    }

    @RouteScoped
    @Route("group2")
    public static class Group2 extends HasElementTestBean {
    }

    private UIUnderTestContext uiUnderTestContext;

    @Inject
    private Provider<Group1> group1;

    @Inject
    @RouteScopeOwner(Group1.class)
    private Provider<MemberOfGroup1> memberOfGroup1;

    @Inject
    private Provider<Group2> group2;

    @Inject
    private Event<AfterNavigationEvent> afterNavigationTrigger;

    private List<HasElement> chain;
    private AfterNavigationEvent event;

    @Before
    public void setUp() {
        uiUnderTestContext = new UIUnderTestContext();
        uiUnderTestContext.activate();

        group1.get().setState(STATE);
        group2.get().setState(STATE);
        memberOfGroup1.get().setState(STATE);

        assertEquals(STATE, group1.get().getState());
        assertEquals(STATE, memberOfGroup1.get().getState());
        assertEquals(STATE, group2.get().getState());

        chain = new ArrayList<>();
        event = Mockito.mock(AfterNavigationEvent.class);
        Mockito.when(event.getActiveChain()).thenReturn(chain);
    }

    @After
    public void tearDown() {
        uiUnderTestContext.tearDownAll();
    }

    @Test
    public void onAfterNavigation_chainIsEmpty_allDestroyed() {
        afterNavigationTrigger.fire(event);

        assertEquals("", group1.get().getState());
        assertEquals("", memberOfGroup1.get().getState());
        assertEquals("", group2.get().getState());
    }

    @Test
    public void onAfterNavigation_chainDoesNotContainOwner_ownerDestroyedOtherRemained() {
        chain.add(group1.get());
        afterNavigationTrigger.fire(event);

        assertEquals(STATE, group1.get().getState());
        assertEquals(STATE, memberOfGroup1.get().getState());
        assertEquals("", group2.get().getState());
    }

    @Test
    public void onAfterNavigation_chainDoesNotContainOwnerForAssigned_bothDestroyedOtherRemained() {
        chain.add(group2.get());
        chain.add(memberOfGroup1.get());
        afterNavigationTrigger.fire(event);

        assertEquals("", group1.get().getState());
        assertEquals("", memberOfGroup1.get().getState());
        assertEquals(STATE, group2.get().getState());
    }

}
