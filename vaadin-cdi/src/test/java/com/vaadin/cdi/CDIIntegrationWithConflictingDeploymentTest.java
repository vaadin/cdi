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

import com.vaadin.cdi.uis.PlainColidingAlternativeUI;
import com.vaadin.cdi.uis.PlainUI;
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
public class CDIIntegrationWithConflictingDeploymentTest extends
        AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment(name = "alternativeUiPathCollision", testable = false)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("alternativeUiPathCollision",
                PlainUI.class, PlainColidingAlternativeUI.class);
    }

    @Test
    @OperateOnDeployment("alternativeUiPathCollision")
    public void alternativeDoesNotColideWithPath() throws IOException {
        final String plainUIPath = deriveMappingForUI(PlainUI.class);
        final String plainAlternativeUI = deriveMappingForUI(PlainColidingAlternativeUI.class);
        assertThat(plainUIPath, is(plainAlternativeUI));
        openWindow(plainUIPath);
        assertThat(getCount(PlainUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(PlainColidingAlternativeUI.CONSTRUCT_COUNT), is(0));
    }
}
