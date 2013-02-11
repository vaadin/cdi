/*
 * Copyright 2012 Vaadin Ltd.
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

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.cdi.uis.*;
import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.SeleniumException;

@RunAsClient
@RunWith(Arquillian.class)
public class CDIIntegrationWithVaadinIT {

    @Drone
    GrapheneSelenium firstWindow;

    @ArquillianResource
    URL contextPath;

    @ArquillianResource
    private Deployer deployer;

    private final static IdLocator LABEL = id("label");
    private final static IdLocator BUTTON = id("button");
    private final static IdLocator NAVIGATE_BUTTON = id("navigate");
    private final static String UI_URI = "instrumentedUI";
    private final static String PLAIN_UI_URI = "plainUI";
    private final static String UI_WITH_CDISELF_LISTENER = "uIWithCDISelfListener";
    private final static String INTERCEPTED_UI = "interceptedUI";
    private static final String UI_WITH_CDI_DEPENDENT_LISTENER = "uIWithCDIDependentListener";

    private final static String SECOND_UI_URI = "secondUI";
    private final static String UNSECURED_UI_URI = "unsecuredUI";
    private final static String INSTRUMENTED_VIEW_URI = UI_URI
            + "/#!instrumentedView";
    private final static String DANGLING_VIEW_URI = SECOND_UI_URI
            + "/#!danglingView";

    @Deployment
    public static WebArchive archiveWithDefaultRootUI() {
        return ArchiveProvider.createWebArchive("default",InstrumentedUI.class,
                InstrumentedView.class, ScopedInstrumentedView.class,
                ViewWithoutAnnotation.class, RootUI.class, FirstUI.class,
                SecondUI.class, UnsecuredUI.class,
                WithAnnotationRegisteredView.class,
                UIWithCDISelfListener.class, UIWithCDIDependentListener.class,
                DependentCDIEventListener.class, InterceptedUI.class,
                InstrumentedInterceptor.class, InterceptedBean.class,
                RestrictedView.class, PlainUI.class, ParameterizedNavigationUI.class,
                EnterpriseUI.class,Boundary.class,SubUI.class,PlainAlternativeUI.class);
    }

    @Deployment(name = "customURIMapping")
    public static WebArchive archiveWithCustomURIMapping() {
        return ArchiveProvider.createWebArchive("custom",RootWithCustomMappingUI.class);
    }

    @Deployment(name = "multipleRoots",managed = false)
    public static WebArchive multipleRootsInWar() {
        return ArchiveProvider.createWebArchive("multipleroots",RootWithCustomMappingUI.class,RootUI.class);
    }

    @Deployment(name = "uiPathCollision",managed = false)
    public static WebArchive multipleUIsWithSamePath() {
        return ArchiveProvider.createWebArchive("uiPathCollision",PathCollisionUI.class,AnotherPathCollisionUI.class);
    }

    @Deployment(name = "alternativeUiPathCollision")
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("alternativeUiPathCollision",PlainUI.class,PlainColidingAlternativeUI.class);
    }

    @Before
    public void resetCounter() {
        PlainUI.resetCounter();
        InstrumentedUI.resetCounter();
        InstrumentedView.resetCounter();
        ScopedInstrumentedView.resetCounter();
        ViewWithoutAnnotation.resetCounter();
        WithAnnotationRegisteredView.resetCounter();
        SecondUI.resetCounter();
        FirstUI.resetCounter();
        RootUI.resetCounter();
        RootWithCustomMappingUI.resetCounter();
        UIWithCDIDependentListener.resetCounter();
        UIWithCDISelfListener.resetCounter();
        DependentCDIEventListener.resetCounter();
        DependentCDIEventListener.resetEventCounter();
        ParameterizedNavigationUI.reset();
        firstWindow.restartBrowser();

    }

    private void openWindow(String uri) throws MalformedURLException {
        openWindow(this.firstWindow, uri);
    }

    void openWindow(GrapheneSelenium window, String uri)
            throws MalformedURLException {
        openWindowNoWait(window,uri);
        waitModel.until(elementPresent.locator(LABEL));
    }

    void openWindowNoWait(String uri) throws MalformedURLException {
        openWindowNoWait(this.firstWindow,uri);
    }

    void openWindowNoWait(GrapheneSelenium window, String uri)
            throws MalformedURLException {
        URL url = new URL(contextPath.toString() + uri);
        window.open(url);
    }

    @Test
    public void browserRestartCreatesNewInstance()
            throws MalformedURLException {
        openWindow(PLAIN_UI_URI);
        assertTrue("PlainUI should contain a label",
                firstWindow.isElementPresent(LABEL));
        assertThat(PlainUI.getNumberOfInstances(), is(1));
        // reset session
        firstWindow.restartBrowser();
        openWindow(PLAIN_UI_URI);
        assertTrue("PlainUI should contain a label",
                firstWindow.isElementPresent(LABEL));
        assertThat(PlainUI.getNumberOfInstances(), is(2));
        assertDefaultRootNotInstantiated();

    }

    @Test
    public void oneToOneRelationBetweenBrowserAndUI()
            throws MalformedURLException {

        openWindow(UI_URI);

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        int clickCount = number(firstWindow.getText(LABEL));
        assertThat(clickCount, is(1));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        clickCount = number(firstWindow.getText(LABEL));
        assertThat(clickCount, is(2));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));

        firstWindow.restartBrowser();
        openWindow(UI_URI);

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        clickCount = number(firstWindow.getText(LABEL));
        assertThat(clickCount, is(1));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        clickCount = number(firstWindow.getText(LABEL));
        assertThat(clickCount, is(2));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void dependentScopedViewIsInstantiatedTwice()
            throws MalformedURLException {
        openWindow(firstWindow, INSTRUMENTED_VIEW_URI);
        firstWindow.click(NAVIGATE_BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(InstrumentedView.getNumberOfInstances(), is(2));
    }

    @Test
    public void recognitionOfViewWithoutAnnotation()
            throws MalformedURLException {
        ParameterizedNavigationUI.NAVIGATE_TO = "viewWithoutAnnotation";
        openWindow(Conventions.deriveMappingForUI(ParameterizedNavigationUI.class));
        firstWindow.click(NAVIGATE_BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(ViewWithoutAnnotation.getNumberOfInstances(), is(1));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void rootUIDiscovery() throws MalformedURLException {
        assertThat(RootUI.getNumberOfInstances(), is(0));
        openWindow("");
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(RootUI.getNumberOfInstances(), is(1));
    }


    @Test
    public void uiInheritance() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(SubUI.class));
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(SubUI.getNumberOfInstances(), is(1));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void refreshButtonCreatesNewUIInstance()
            throws MalformedURLException {
        openWindow(UI_URI);
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));
        firstWindow.refresh();
        waitModel.until(elementPresent.locator(LABEL));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void danglingViewCauses404() throws MalformedURLException {
        openWindow(DANGLING_VIEW_URI);
        waitModel.until(elementPresent.locator(LABEL));
        firstWindow.click(NAVIGATE_BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(SecondUI.getNumberOfInstances(), is(1));
        assertThat(DanglingView.getNumberOfInstances(), is(0));
    }

    @Test  @OperateOnDeployment("alternativeUiPathCollision")
    public void alternativeDoesNotColideWithPath() throws MalformedURLException {
        final String plainUIPath = Conventions.deriveMappingForUI(PlainUI.class);
        final String plainAlternativeUI = Conventions.deriveMappingForUI(PlainColidingAlternativeUI.class);
        assertThat(plainUIPath,is(plainAlternativeUI));
        openWindow(plainUIPath);
        waitModel.until(elementPresent.locator(LABEL));
        assertThat(PlainUI.getNumberOfInstances(), is(1));
        assertThat(PlainColidingAlternativeUI.getNumberOfInstances(), is(0));
    }

    @Test
    public void alternativeIsNotAccessible() throws MalformedURLException {
        openWindowNoWait(Conventions.deriveMappingForUI(PlainAlternativeUI.class));
        final String expectedErrorMessage = this.firstWindow.getBodyText();
        assertThat(expectedErrorMessage, containsString("404"));
        assertThat(PlainAlternativeUI.getNumberOfInstances(), is(0));
    }

    @Test
    public void cdiEventsArrivesInTheSameUIScopedInstance()
            throws MalformedURLException {
        assertThat(UIWithCDISelfListener.getNumberOfInstances(), is(0));
        assertThat(UIWithCDISelfListener.getNumberOfDeliveredEvents(), is(0));
        openWindow(UI_WITH_CDISELF_LISTENER);
        waitModel.until(elementPresent.locator(LABEL));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(UIWithCDISelfListener.getNumberOfInstances(), is(1));
        assertThat(UIWithCDISelfListener.getNumberOfDeliveredEvents(), is(1));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(UIWithCDISelfListener.getNumberOfInstances(), is(1));
        assertThat(UIWithCDISelfListener.getNumberOfDeliveredEvents(), is(2));

    }

    @Test
    public void cdiEventsArrivesInDependentListener()
            throws MalformedURLException {
        assertThat(UIWithCDIDependentListener.getNumberOfInstances(), is(0));
        assertThat(DependentCDIEventListener.getNumberOfDeliveredEvents(),
                is(0));
        assertThat(DependentCDIEventListener.getNumberOfInstances(), is(0));
        openWindow(UI_WITH_CDI_DEPENDENT_LISTENER);
        waitModel.until(elementPresent.locator(LABEL));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(UIWithCDIDependentListener.getNumberOfInstances(), is(1));
        assertThat(DependentCDIEventListener.getNumberOfInstances(), is(1));
        assertThat(DependentCDIEventListener.getNumberOfDeliveredEvents(),
                is(1));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(UIWithCDIDependentListener.getNumberOfInstances(), is(1));
        assertThat(DependentCDIEventListener.getNumberOfInstances(), is(2));
        assertThat(DependentCDIEventListener.getNumberOfDeliveredEvents(),
                is(2));

    }

    @Test
    public void interceptedScopedEventListener() throws MalformedURLException {
        assertThat(InterceptedUI.getNumberOfInstances(), is(0));
        assertThat(InstrumentedInterceptor.getCounter(), is(0));
        openWindow(INTERCEPTED_UI);
        waitModel.until(elementPresent.locator(LABEL));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(InstrumentedInterceptor.getCounter(), is(1));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(InstrumentedInterceptor.getCounter(), is(2));

    }

    @Test
    public void navigationToRestrictedViewFails() throws MalformedURLException {
        assertThat(ParameterizedNavigationUI.getNumberOfInstances(), is(0));
        assertThat(RestrictedView.getNumberOfInstances(), is(0));
        ParameterizedNavigationUI.NAVIGATE_TO = Conventions.deriveMappingForView(RestrictedView.class);
        openWindow(Conventions.deriveMappingForUI(ParameterizedNavigationUI.class));
        firstWindow.click(NAVIGATE_BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(ParameterizedNavigationUI.getNumberOfInstances(), is(1));
        assertThat(RestrictedView.getNumberOfInstances(), is(0));
        assertDefaultRootNotInstantiated();

    }

    @Test @OperateOnDeployment("customURIMapping")
    public void customServletMapping() throws MalformedURLException {
        assertThat(RootWithCustomMappingUI.getNumberOfInstances(), is(0));
        openWindow("customURI/rootWithCustomMappingUI");
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(RootWithCustomMappingUI.getNumberOfInstances(), is(1));

    }

    /**
     *
     * Tests invalid deployment of multiple roots within a WAR
     * Should be before the regular tests--arquillian deployments are not perfectly isolated.
     */
    @Test @InSequence(-1)
    public void multipleRootsBreakDeployment() throws MalformedURLException {
        assertThat(RootUI.getNumberOfInstances(), is(0));
        deployer.deploy("multipleRoots");
        openWindowNoWait("");
        final String expectedErrorMessage = this.firstWindow.getBodyText();
        assertThat(expectedErrorMessage, containsString("VaadinCDIServlet deployment aborted. Reason:"));
        assertThat(RootUI.getNumberOfInstances(), is(0));

    }

    /**
     *
     * Tests invalid deployment of multiple roots within a WAR
     * Should be started first--arquillian deployments are not perfectly isolated.
     */
    @Test @InSequence(-2)
    public void uiPathCollisionBreaksDeployment() throws MalformedURLException {
        assertThat(RootUI.getNumberOfInstances(), is(0));
        deployer.deploy("uiPathCollision");
        openWindowNoWait(Conventions.deriveMappingForUI(PathCollisionUI.class));
        final String expectedErrorMessage = this.firstWindow.getBodyText();
        assertThat(expectedErrorMessage, containsString("VaadinCDIServlet deployment aborted. Reason:"));
        assertThat(RootUI.getNumberOfInstances(), is(0));

    }

    @Test
    public void ejbInvocation() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(EnterpriseUI.class));
        waitModel.waitForChange(retrieveText.locator(LABEL));
        assertThat(EnterpriseUI.getNumberOfInstances(), is(1));
        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        final String labelText = retrieveText.locator(LABEL).retrieve();
        assertThat(labelText,startsWith("Echo:"));
        assertThat(EnterpriseUI.getNumberOfInstances(), is(1));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void unsecuredUI() throws MalformedURLException {
        openWindow(UNSECURED_UI_URI);
        final String principalName = firstWindow.getText(id("principalName"));
        final String isUserInRole = firstWindow.getText(id("isUserInRole"));
        final String isUserInSomeRole = firstWindow
                .getText(id("isUserInSomeRole"));
        final String currentRequestNotNull = firstWindow
                .getText(id("currentRequestNotNull"));
        final String isUserSignedIn = firstWindow.getText(id("isUserSignedIn"));
        final String disabled = firstWindow.getText(id("disabled"));
        try {
            firstWindow.getText(id("invisible"));
            fail("Invisible element should not be accessible");
        } catch (SeleniumException ex) {
        }
        assertFalse(Boolean.parseBoolean(principalName));
        assertFalse(Boolean.parseBoolean(isUserInRole));
        assertFalse(Boolean.parseBoolean(isUserInSomeRole));
        assertTrue(Boolean.parseBoolean(currentRequestNotNull));
        assertFalse(Boolean.parseBoolean(isUserSignedIn));
        assertThat(disabled, is("DisabledLabel"));
    }

    void assertDefaultRootNotInstantiated() {
        assertThat(RootUI.getNumberOfInstances(), is(0));
    }

    public int number(String txt) {
        System.out.println("Text: " + txt);
        return Integer.parseInt(txt);
    }
}
