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
import com.vaadin.cdi.uis.RootUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

@RunAsClient
@RunWith(Arquillian.class)
public class MultipleRootUIsTest extends AbstractCDIIntegrationTest {

    @Deployment(name = "multipleRoots", managed = false, testable = false)
    public static WebArchive archiveWithMultipleRoots() {
        return ArchiveProvider.createWebArchive("multipleRoots", RootUI.class,
                CustomMappingUI.class);
    }

    /**
     * Tests invalid deployment of multiple roots within a WAR Should be before
     * the regular tests -- Arquillian deployments are not perfectly isolated.
     */
    @Test(expected = Exception.class)
    public void multipleRootsBreakDeployment() throws Exception {
        deployer.deploy("multipleRoots");
        fail("Multiple roots should not be deployable");
    }

}
