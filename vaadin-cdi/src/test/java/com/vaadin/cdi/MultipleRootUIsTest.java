/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.cdi;

import com.vaadin.cdi.uis.CustomMappingUI;
import com.vaadin.cdi.uis.RootUI;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;

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
    @Test
    public void multipleRootsBreakDeployment() throws MalformedURLException {
        try {
            deployer.deploy("multipleRoots");
            fail("Multiple roots should not be deployable");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
        }
    }

}
