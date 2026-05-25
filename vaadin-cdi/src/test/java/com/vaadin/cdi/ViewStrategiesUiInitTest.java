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

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.ViewStrategyInitUI;
import com.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

//@Ignore("Arquillian integration test - requires an application server container profile and browser")
public class ViewStrategiesUiInitTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategiesUiInit", ViewStrategyInitUI.class);
    }

    @Before
    public void setUp() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(ViewStrategyUI.class)+"#!home/p1";
        openWindow(viewUri);
    }

    @Test
    public void testViewNameStrategyHasRightStateAfterUiInit() throws Exception {
        clickAndWait(ViewStrategyInitUI.VIEWNAME_BTN_ID);
        final String result = findElement(ViewStrategyInitUI.OUTPUT_ID).getText();
        Assert.assertEquals("true", result);
    }

    @Test
    public void testViewNameAndParametersStrategyHasRightStateAfterUiInit() throws Exception {
        clickAndWait(ViewStrategyInitUI.VIEWNAMEPARAMS_BTN_ID);
        final String result = findElement(ViewStrategyInitUI.OUTPUT_ID).getText();
        Assert.assertEquals("true", result);
    }
}
