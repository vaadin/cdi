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

import com.vaadin.cdi.uis.CustomMappingUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

//@Ignore("Arquillian integration test - requires an application server container profile and browser")
public class CDIIntegrationWithCustomDeploymentTest extends
        AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment(name = "customURIMapping", testable = false)
    public static WebArchive archiveWithCustomURIMapping() {
        return ArchiveProvider
                .createWebArchive("custom", CustomMappingUI.class);
    }

    @Test
    @OperateOnDeployment("customURIMapping")
    public void customServletMapping() throws IOException {
        assertThat(getCount(CustomMappingUI.CONSTRUCT_COUNT), is(0));
        openWindow("customURI/");
        assertThat(getCount(CustomMappingUI.CONSTRUCT_COUNT), is(1));
    }
}
