/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.smoke.CdiView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("ArquillianTooManyDeployment")
public class SmokeTest extends AbstractCdiTest {

    @Deployment(name = "noncdi", testable = false)
    public static WebArchive createCdiServletDisabledDeployment() {
        return ArchiveProvider.createWebArchive("noncdi-test",
                CdiView.class)
                .addAsWebInfResource(ArchiveProvider.class.getClassLoader()
                        .getResource("disablecdi-web.xml"), "web.xml");
    }

    @Deployment(name = "cdi", testable = false)
    public static WebArchive createCdiServletEnabledDeployment() {
        return ArchiveProvider.createWebArchive("cdi-test",
                CdiView.class);
    }

    @Before
    public void setUp() {
        open();
        $("button").first().click();
    }

    @Test
    @OperateOnDeployment("noncdi")
    public void injectionDoesNotHappenWithDisabledCdiServlet() {
        assertLabelEquals("no CDI");
    }

    @Test
    @OperateOnDeployment("cdi")
    public void injectionHappensWithEnabledCdiServlet() {
        assertLabelEquals("hello CDI");
    }

    private void assertLabelEquals(String expected) {
        Assert.assertEquals(expected, $("label").first().getText());
    }

}
