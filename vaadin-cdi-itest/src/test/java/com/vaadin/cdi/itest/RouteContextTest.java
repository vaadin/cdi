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

package com.vaadin.cdi.itest;

import java.io.File;
import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.cdi.itest.routecontext.ApartBean;
import com.vaadin.cdi.itest.routecontext.AssignedBean;
import com.vaadin.cdi.itest.routecontext.DetailApartView;
import com.vaadin.cdi.itest.routecontext.DetailAssignedView;
import com.vaadin.cdi.itest.routecontext.ErrorHandlerView;
import com.vaadin.cdi.itest.routecontext.ErrorParentView;
import com.vaadin.cdi.itest.routecontext.ErrorView;
import com.vaadin.cdi.itest.routecontext.EventView;
import com.vaadin.cdi.itest.routecontext.MasterView;
import com.vaadin.cdi.itest.routecontext.PostponeView;
import com.vaadin.cdi.itest.routecontext.RerouteView;
import com.vaadin.cdi.itest.routecontext.RootView;

public class RouteContextTest extends AbstractCdiTest {

    private String uiId;

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("route-context",
                webArchive -> webArchive
                        .addPackage(MasterView.class.getPackage())
                        .addAsResource(new File("target/classes/META-INF")));
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        open("");
        uiId = getText(RootView.UIID);
        assertConstructed(RootView.class, 1);
        assertDestroyed(RootView.class, 0);
        assertConstructed(RerouteView.class, 0);
        assertConstructed(MasterView.class, 0);
        assertConstructed(AssignedBean.class, 0);
        assertConstructed(ApartBean.class, 0);
        assertConstructed(DetailApartView.class, 0);
        assertConstructed(DetailAssignedView.class, 0);
        assertConstructed(ErrorParentView.class, 0);
        assertConstructed(ErrorHandlerView.class, 0);
    }

    @Test
    public void navigateFromRootToMasterReleasesRootInjectsEmptyBeans()
            throws IOException {
        follow(RootView.MASTER);
        assertTextEquals("", MasterView.ASSIGNED_BEAN_LABEL);
        assertTextEquals("", MasterView.APART_BEAN_LABEL);

        assertConstructed(RootView.class, 1);
        assertDestroyed(RootView.class, 1);
        assertConstructed(MasterView.class, 1);
        assertDestroyed(MasterView.class, 0);
        assertConstructed(AssignedBean.class, 1);
        assertDestroyed(AssignedBean.class, 0);
        assertConstructed(ApartBean.class, 1);
        assertDestroyed(ApartBean.class, 0);
        assertConstructed(DetailApartView.class, 0);
        assertConstructed(DetailAssignedView.class, 0);
    }

    @Test
    public void navigationFromAssignedToMasterHoldsGroup() throws IOException {
        follow(RootView.MASTER);
        follow(MasterView.ASSIGNED);
        assertTextEquals("ASSIGNED", DetailAssignedView.BEAN_LABEL);
        assertTextEquals("", MasterView.APART_BEAN_LABEL);

        follow(DetailAssignedView.MASTER);
        assertConstructed(MasterView.class, 1);
        assertDestroyed(MasterView.class, 0);
        assertConstructed(DetailAssignedView.class, 1);
        assertDestroyed(DetailAssignedView.class, 0);
        assertConstructed(DetailApartView.class, 0);

        assertTextEquals("ASSIGNED", MasterView.ASSIGNED_BEAN_LABEL);
        assertTextEquals("", MasterView.APART_BEAN_LABEL);
    }

    @Test
    public void navigationFromApartToMasterReleasesGroup() throws IOException {
        follow(RootView.MASTER);
        follow(MasterView.APART);
        assertTextEquals("", MasterView.ASSIGNED_BEAN_LABEL);
        assertTextEquals("APART", DetailApartView.BEAN_LABEL);

        follow(DetailApartView.MASTER);
        assertConstructed(MasterView.class, 1);
        assertDestroyed(MasterView.class, 0);
        assertConstructed(DetailAssignedView.class, 0);
        assertDestroyed(DetailAssignedView.class, 0);
        assertConstructed(DetailApartView.class, 1);
        assertDestroyed(DetailApartView.class, 1);

        assertTextEquals("", MasterView.ASSIGNED_BEAN_LABEL);
        assertTextEquals("", MasterView.APART_BEAN_LABEL);
    }

    @Test
    public void rerouteReleasesSource() throws IOException {
        follow(RootView.REROUTE);
        assertConstructed(RerouteView.class, 1);
        assertDestroyed(RerouteView.class, 1);

        assertRootViewIsDisplayed();
    }

    @Test
    public void postponedNavigationDoesNotCreateTarget() throws IOException {
        follow(RootView.POSTPONE);
        assertConstructed(RootView.class, 1);

        follow(PostponeView.POSTPONED_ROOT);
        assertConstructed(RootView.class, 1);
        assertDestroyed(RootView.class, 1);

        click(PostponeView.NAVIGATE);
        assertConstructed(RootView.class, 2);
        assertDestroyed(RootView.class, 1);
        assertRootViewIsDisplayed();
    }

    @Test
    public void eventObserved() {
        follow(RootView.EVENT);
        assertTextEquals("", EventView.OBSERVER_LABEL);

        click(EventView.FIRE);
        assertTextEquals("HELLO", EventView.OBSERVER_LABEL);
    }

    @Test
    @Ignore("Temprary disabled since it doesn't work with CCDM: https://github.com/vaadin/cdi/issues/314")
    public void errorHandlerIsScoped() throws IOException {
        follow(RootView.ERROR);
        assertConstructed(RootView.class, 1);
        assertDestroyed(RootView.class, 1);
        assertConstructed(ErrorView.class, 1);
        assertDestroyed(ErrorView.class, 1);
        assertConstructed(ErrorParentView.class, 1);
        assertDestroyed(ErrorParentView.class, 0);
        assertConstructed(ErrorHandlerView.class, 1);
        assertDestroyed(ErrorHandlerView.class, 0);

        follow(ErrorHandlerView.PARENT);
        assertConstructed(ErrorParentView.class, 1);
        assertDestroyed(ErrorParentView.class, 0);
        assertConstructed(ErrorHandlerView.class, 1);
        assertDestroyed(ErrorHandlerView.class, 0);

        follow(ErrorParentView.ROOT);
        assertConstructed(RootView.class, 2);
        assertDestroyed(RootView.class, 1);
        assertConstructed(ErrorParentView.class, 1);
        assertDestroyed(ErrorParentView.class, 1);
        assertConstructed(ErrorHandlerView.class, 1);
        assertDestroyed(ErrorHandlerView.class, 1);

        assertRootViewIsDisplayed();
    }

    private void assertRootViewIsDisplayed() {
        assertTextEquals(uiId, RootView.UIID);
    }

    private void assertConstructed(Class beanClass, int count)
            throws IOException {
        Assert.assertEquals(count,
                getCount(beanClass.getSimpleName() + "C" + uiId));
    }

    private void assertDestroyed(Class beanClass, int count)
            throws IOException {
        Assert.assertEquals(count,
                getCount(beanClass.getSimpleName() + "D" + uiId));
    }

}
