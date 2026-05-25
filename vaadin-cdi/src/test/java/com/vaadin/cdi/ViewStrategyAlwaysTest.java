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

import com.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Ignore;

//@Ignore("Arquillian integration test - requires an application server container profile and browser")
public class ViewStrategyAlwaysTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyAlways", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYALWAYS + "/p1",
                ViewStrategyUI.BYALWAYS + "/p1",
                ",byalways:p1",
                ",byalways:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYALWAYS,
                ViewStrategyUI.BYALWAYS,
                ",byalways:",
                ",byalways:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYALWAYS + "/p1",
                ViewStrategyUI.BYALWAYS + "/p2",
                ",byalways:p1",
                ",byalways:p2"
        );
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertAfterNavigateToOtherViewContextCreated(
                ViewStrategyUI.BYALWAYS,
                ",byalways:"
        );
    }

    @Override
    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByAlwaysView.DESTROY_COUNT;
    }

    @Override
    protected String getTestedViewConstructCounter() {
        return ViewStrategyUI.ByAlwaysView.CONSTRUCT_COUNT;
    }
}
