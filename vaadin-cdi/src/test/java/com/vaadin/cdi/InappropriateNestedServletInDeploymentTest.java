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

import com.vaadin.cdi.uis.UIWithNestedServlet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.junit.Assert.fail;

public class InappropriateNestedServletInDeploymentTest extends
        AbstractCDIIntegrationTest {

    @Deployment(name = "nestedServlet", managed = false, testable = false)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("nestedServlet",
                UIWithNestedServlet.class);
    }

    /**
     * Tests invalid deployment nested servlet within a UI class. Should be
     * started first -- Arquillian deployments are not perfectly isolated.
     */
    @Test(expected = Exception.class)
    @InSequence(-2)
    public void nestedServletBreaksDeployment() throws Exception {
        deployer.deploy("nestedServlet");
        fail("Servlet class nested in the UI should not be deployable");
    }

}
