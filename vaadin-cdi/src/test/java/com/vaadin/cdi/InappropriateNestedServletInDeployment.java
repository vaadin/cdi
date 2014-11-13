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

import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import com.vaadin.cdi.uis.UIWithNestedServlet;

public class InappropriateNestedServletInDeployment extends
        AbstractCDIIntegrationTest {

    @Deployment(name = "nestedServlet", managed = false)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("nestedServlet",
                UIWithNestedServlet.class);
    }

    /**
     * Tests invalid deployment nested servlet within a UI class. Should be
     * started first -- Arquillian deployments are not perfectly isolated.
     */
    @Test
    @InSequence(-2)
    public void nestedServletBreaksDeployment() throws MalformedURLException {
        try {
            System.out.println("DEPLOYING");
            // Deployment doesn't declare that it can throw DeploymentException
            // De-facto it can
            deployer.deploy("nestedServlet");
            System.out.println("Deployed");
            fail("Servlet class nested in the UI should not be deployable");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
            System.out.println("Exiting try block");
        }
    }

}
