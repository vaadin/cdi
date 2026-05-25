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
public class ViewStrategyViewNameAndParamsTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyViewNameAndParams", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p1",
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p1",
                ",byviewnameparams:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAMEPARAMS,
                ViewStrategyUI.BYVIEWNAMEPARAMS,
                ",byviewnameparams:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p1",
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p2",
                ",byviewnameparams:p1",
                ",byviewnameparams:p2"
        );
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertAfterNavigateToOtherViewContextCreated(
                ViewStrategyUI.BYVIEWNAMEPARAMS,
                ",byviewnameparams:"
        );
    }

    @Override
    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByViewNameAndParametersView.DESTROY_COUNT;
    }

    @Override
    protected String getTestedViewConstructCounter() {
        return ViewStrategyUI.ByViewNameAndParametersView.CONSTRUCT_COUNT;
    }
}
