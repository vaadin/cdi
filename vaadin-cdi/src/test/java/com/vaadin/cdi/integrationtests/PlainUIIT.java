/*
 * Copyright 2013 Vaadin Ltd.
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
package com.vaadin.cdi.integrationtests;

import static com.vaadin.cdi.internal.Conventions.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.jboss.arquillian.ajocado.Graphene.elementPresent;
import static org.jboss.arquillian.ajocado.Graphene.id;
import static org.jboss.arquillian.ajocado.Graphene.retrieveText;
import static org.jboss.arquillian.ajocado.Graphene.waitModel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.vaadin.cdi.integrationtests.uis.PlainUI;
import java.net.MalformedURLException;
import java.net.URL;
import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration test that contains very simple test cases. Its primary purpose is
 * to teach me how to use Arquillian.
 */
@RunAsClient
@RunWith(Arquillian.class)
public class PlainUIIT {

    @Drone
    GrapheneSelenium firstWindow;
    @ArquillianResource
    URL contextPath;
    @ArquillianResource
    private Deployer deployer;
    private final static IdLocator LABEL = id("label");

    @Deployment
    public static WebArchive war() {
        return ArchiveProvider.createWebArchive(PlainUIIT.class.getSimpleName(),
                PlainUI.class);
    }

    @Before
    public void setUp() {
        PlainUI.resetCounter();
        firstWindow.restartBrowser();
    }

    private void openFirstWindow(String uri) throws MalformedURLException {
        openWindow(this.firstWindow, uri);
    }

    void openWindow(GrapheneSelenium window, String uri)
            throws MalformedURLException {
        openWindowNoWait(window, uri);
        waitModel.until(elementPresent.locator(LABEL));
    }

    void openFirstWindowNoWait(String uri) throws MalformedURLException {
        openWindowNoWait(this.firstWindow, uri);
    }

    void openWindowNoWait(GrapheneSelenium window, String uri)
            throws MalformedURLException {
        URL url = new URL(contextPath.toString() + uri);
        window.open(url);
    }

    @Test
    public void openSingleTab() throws MalformedURLException {
        String uri = deriveMappingForUI(PlainUI.class);
        openFirstWindow(uri);
        assertTrue("PlainUI should contain a label", firstWindow.isElementPresent(LABEL));
        assertThat(PlainUI.getNumberOfInstances(), is(1));
    }
}
