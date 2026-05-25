/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import com.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Ignore;

//@Ignore("Arquillian integration test - requires an application server container profile and browser")
public class ViewStrategyViewNameTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyViewName", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAME + "/p1",
                ViewStrategyUI.BYVIEWNAME + "/p1",
                ",byviewname:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAME,
                ViewStrategyUI.BYVIEWNAME,
                ",byviewname:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersHoldsContext() throws Exception {
        navigateTo(ViewStrategyUI.BYVIEWNAME + "/p1");
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertBeanValue(",byviewname:p1");

        navigateTo(ViewStrategyUI.BYVIEWNAME + "/p2");
        // context hold
        assertConstructCounts(1);
        assertDestroyCounts(0);
        // navigation happened - init called again
        assertBeanValue(",byviewname:p1,byviewname:p2");
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertAfterNavigateToOtherViewContextCreated(
                ViewStrategyUI.BYVIEWNAME,
                ",byviewname:"
        );
    }

    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByViewNameView.DESTROY_COUNT;
    }

    protected String getTestedViewConstructCounter() {
        return ViewStrategyUI.ByViewNameView.CONSTRUCT_COUNT;
    }

}
