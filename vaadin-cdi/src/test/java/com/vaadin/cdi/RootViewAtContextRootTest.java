/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import com.vaadin.cdi.uis.ParameterizedNavigationUI;
import com.vaadin.cdi.views.RootView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.io.IOException;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

//@Ignore("Arquillian integration test - requires an application server container profile and browser")
public class RootViewAtContextRootTest extends AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment(name = "rootView", testable = false)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("rootView", RootView.class,
                ParameterizedNavigationUI.class);
    }

    @Test
    @OperateOnDeployment("rootView")
    public void testThatRootViewIsReachable() throws IOException {
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(RootView.CONSTRUCT_COUNT), is(0));
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class) +
                ParameterizedNavigationUI.getNavigateToParam(""));
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "default view");
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(RootView.CONSTRUCT_COUNT), is(1));
    }
}
