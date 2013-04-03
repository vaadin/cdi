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

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.arquillian.ajocado.Graphene.elementPresent;
import static org.jboss.arquillian.ajocado.Graphene.id;
import static org.jboss.arquillian.ajocado.Graphene.waitModel;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.uis.RootUI;
import com.vaadin.cdi.uis.RootWithCustomMappingUI;

@RunAsClient
@RunWith(Arquillian.class)
public class MultipleRootUIsIT {

    @Drone
    GrapheneSelenium firstWindow;

    @ArquillianResource
    URL contextPath;

    @ArquillianResource
    private Deployer deployer;

    private final static IdLocator LABEL = id("label");

    @Deployment(name = "multipleRoots")
    public static WebArchive archiveWithMultipleRoots() {
        return ArchiveProvider.createWebArchive("multipleRoots", RootUI.class,
                RootWithCustomMappingUI.class);
    }

    @Before
    public void resetCounter() {
        RootUI.resetCounter();
        RootWithCustomMappingUI.resetCounter();
        firstWindow.restartBrowser();

    }

    void openWindow(GrapheneSelenium window, String uri)
            throws MalformedURLException {
        openWindowNoWait(window, uri);
        waitModel.until(elementPresent.locator(LABEL));
    }

    void openWindowNoWait(String uri) throws MalformedURLException {
        openWindowNoWait(firstWindow, uri);
    }

    void openWindowNoWait(GrapheneSelenium window, String uri)
            throws MalformedURLException {
        URL url = new URL(contextPath.toString() + uri);
        window.open(url);
    }

    /**
     * Tests invalid deployment of multiple roots within a WAR Should be before
     * the regular tests -- Arquillian deployments are not perfectly isolated.
     */
    @Test
    @OperateOnDeployment("multipleRoots")
    public void multipleRootsBreakDeployment() throws MalformedURLException {
        assertThat(RootUI.getNumberOfInstances(), is(0));
        openWindowNoWait("");
        final String expectedErrorMessage = firstWindow.getBodyText();
        // page not found - the real error message is in the server log
        assertThat(expectedErrorMessage, is(""));
        assertThat(RootUI.getNumberOfInstances(), is(0));
    }
}
