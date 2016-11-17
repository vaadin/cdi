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

import com.vaadin.cdi.uis.*;
import com.vaadin.cdi.views.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.io.IOException;
import java.net.MalformedURLException;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CDIIntegrationWithDefaultDeploymentTest extends
        AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment
    public static WebArchive archiveWithDefaultRootUI() {
        return ArchiveProvider.createWebArchive("default",
                InstrumentedUI.class, InstrumentedView.class,
                ScopedInstrumentedView.class, ViewWithoutAnnotation.class,
                RootUI.class, SecondUI.class, UnsecuredUI.class,
                WithAnnotationRegisteredView.class,
                UIWithCDISelfListener.class, UIWithCDIDependentListener.class,
                DependentCDIEventListener.class, InterceptedUI.class,
                InstrumentedInterceptor.class, InterceptedBean.class,
                RestrictedView.class, PlainUI.class,
                ParameterizedNavigationUI.class, EnterpriseUI.class,
                Boundary.class, EnterpriseLabel.class, SubUI.class,
                PlainAlternativeUI.class, NoViewProviderNavigationUI.class,
                ConventionalView.class, MainView.class, SubView.class,
                AbstractScopedInstancesView.class, AbstractNavigatableView.class,
                NavigatableUI.class);
    }

    @Test
    public void browserRestartCreatesNewInstance() throws Exception {
        String uri = deriveMappingForUI(PlainUI.class);
        openWindow(uri);

        // Throws exception if element not found
        firstWindow.findElement(LABEL);

        assertThat(getCount(PlainUI.CONSTRUCT_COUNT), is(1));

        // reset session
        openWindow(uri);

        // Throws exception if element not found
        firstWindow.findElement(LABEL);

        assertThat(getCount(PlainUI.CONSTRUCT_COUNT), is(2));
        assertDefaultRootNotInstantiated();

    }

    @Test
    public void oneToOneRelationBetweenBrowserAndUI()
            throws IOException {

        openWindow(INSTRUMENTED_UI_URI);

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 1);
        assertThat(getCount(InstrumentedUI.CONSTRUCT_COUNT), is(1));

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 2);
        assertThat(getCount(InstrumentedUI.CONSTRUCT_COUNT), is(1));

        openWindow(INSTRUMENTED_UI_URI);

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 1);
        assertThat(getCount(InstrumentedUI.CONSTRUCT_COUNT), is(2));

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 2);
        assertThat(getCount(InstrumentedUI.CONSTRUCT_COUNT), is(2));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void dependentScopedViewIsInstantiatedTwiceWithViewProvider()
            throws IOException {
        openWindow(firstWindow, INSTRUMENTED_VIEW_URI);
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "ViewLabel");
        assertThat(getCount(InstrumentedView.CONSTRUCT_COUNT), is(2));
    }

    @Test
    public void dependentScopedViewIsInstantiatedOnce()
            throws IOException {
        String uri = deriveMappingForUI(NoViewProviderNavigationUI.class);
        openWindow(uri);
        assertThat(getCount(InstrumentedView.CONSTRUCT_COUNT), is(1));
        firstWindow.findElement(NAVIGATE_BUTTON).click();

        waitForValue(VIEW_LABEL, "ViewLabel");
        assertThat(getCount(InstrumentedView.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(NoViewProviderNavigationUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(NoViewProviderNavigationUI.NAVIGATION_COUNT), is(1));

        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "ViewLabel");
        assertThat(getCount(InstrumentedView.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(NoViewProviderNavigationUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(NoViewProviderNavigationUI.NAVIGATION_COUNT), is(2));

    }

    @Test
    public void rootUIDiscovery() throws IOException {
        assertThat(getCount(RootUI.CONSTRUCT_KEY), is(0));
        openWindow("");
        assertThat(getCount(RootUI.CONSTRUCT_KEY), is(1));
    }

    @Test
    public void uiInheritance() throws Exception {
        openWindow(deriveMappingForUI(SubUI.class));
        assertThat(getCount(PlainUI.CONSTRUCT_COUNT), is(1));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void refreshButtonCreatesNewUIInstance()
            throws IOException {
        openWindow(INSTRUMENTED_UI_URI);
        assertThat(getCount(InstrumentedUI.CONSTRUCT_COUNT), is(1));
        refreshWindow();
        assertThat(getCount(InstrumentedUI.CONSTRUCT_COUNT), is(2));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void danglingViewCauses404() throws IOException {
        openWindow(DANGLING_VIEW_URI);
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        assertThat(getCount(SecondUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(DanglingView.CONSTRUCT_COUNT), is(0));
    }

    @Test
    public void alternativeIsNotAccessible() throws IOException {
        openWindowNoWait(deriveMappingForUI(PlainAlternativeUI.class));
        final String expectedErrorMessage = firstWindow.getPageSource();
        assertThat(expectedErrorMessage, containsString("Request was not handled by any registered handler."));
        assertThat(getCount(PlainAlternativeUI.CONSTRUCT_COUNT), is(0));
    }

    @Test
    public void cdiEventsArrivesInTheSameUIScopedInstance()
            throws IOException, InterruptedException {
        assertThat(getCount(UIWithCDISelfListener.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(UIWithCDISelfListener.EVENT_COUNT), is(0));
        
        openWindow(UIWithCDISelfListener.class);
        
        firstWindow.findElement(BUTTON).click();        
        waitForValue(By.id(UIWithCDISelfListener.MESSAGE_ID), "1 message");
        assertThat(getCount(UIWithCDISelfListener.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(UIWithCDISelfListener.EVENT_COUNT), is(1));
        
        firstWindow.findElement(BUTTON).click();
        waitForValue(By.id(UIWithCDISelfListener.MESSAGE_ID), "2 messages");
        assertThat(getCount(UIWithCDISelfListener.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(UIWithCDISelfListener.EVENT_COUNT), is(2));

    }

    @Test
    public void cdiEventsArrivesInDependentListener()
            throws IOException, InterruptedException {
        assertThat(getCount(UIWithCDIDependentListener.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(DependentCDIEventListener.EVENT_COUNT), is(0));
        assertThat(getCount(DependentCDIEventListener.CONSTRUCT_COUNT), is(0));
        String uri = deriveMappingForUI(UIWithCDIDependentListener.class);
        openWindow(uri);
        firstWindow.findElement(BUTTON).click();
        Thread.sleep(100);
        assertThat(getCount(UIWithCDIDependentListener.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(DependentCDIEventListener.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(DependentCDIEventListener.EVENT_COUNT),
                is(1));
        firstWindow.findElement(BUTTON).click();
        Thread.sleep(100);
        assertThat(getCount(UIWithCDIDependentListener.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(DependentCDIEventListener.CONSTRUCT_COUNT), is(2));
        assertThat(getCount(DependentCDIEventListener.EVENT_COUNT),
                is(2));

    }

    @Test
    public void interceptedScopedEventListener() throws IOException,
            InterruptedException {
        assertThat(getCount(InterceptedUI.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(InstrumentedInterceptor.INTERCEPT_COUNT), is(0));
        String uri = deriveMappingForUI(InterceptedUI.class);
        openWindow(uri);
        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, "hello from intercepted bean");
        assertThat(getCount(InstrumentedInterceptor.INTERCEPT_COUNT), is(1));
        firstWindow.findElement(BUTTON).click();
        Thread.sleep(100);
        assertThat(getCount(InstrumentedInterceptor.INTERCEPT_COUNT), is(2));

    }

    @Test
    public void navigationToRestrictedViewFails() throws IOException {
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(RestrictedView.CONSTRUCT_COUNT), is(0));
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class) +
                ParameterizedNavigationUI.getNavigateToParam("restrictedView"));
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(RestrictedView.CONSTRUCT_COUNT), is(0));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void ejbInvocation() throws IOException {
        openWindow(deriveMappingForUI(EnterpriseUI.class));
        assertThat(getCount(EnterpriseUI.CONSTRUCT_COUNT), is(1));
        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, "Echo: 1");
        final String labelText = firstWindow.findElement(LABEL).getText();
        assertThat(labelText, startsWith("Echo:"));
        final String labelText2 = firstWindow.findElement(
                By.id(EnterpriseLabel.ENTERPRISE_LABEL)).getText();
        assertThat(labelText2, startsWith("Echo:"));
        assertThat(getCount(EnterpriseUI.CONSTRUCT_COUNT), is(1));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void unsecuredUI() throws MalformedURLException {
        // debug mode to work around a strange issue where one of the Labels
        // lacked an ID on the client side
        String uri = deriveMappingForUI(UnsecuredUI.class) + "?debug";
        openWindow(uri);
        final String principalName = firstWindow.findElement(
                By.id("principalName")).getText();
        final String isUserInRole = firstWindow.findElement(
                By.id("isUserInRole")).getText();
        final String isUserInSomeRole = firstWindow.findElement(
                By.id("isUserInSomeRole")).getText();
        final String currentRequestNotNull = firstWindow.findElement(
                By.id("currentRequestNotNull")).getText();
        final String isUserSignedIn = firstWindow.findElement(
                By.id("isUserSignedIn")).getText();
        final String disabled = firstWindow.findElement(By.id("disabled"))
                .getText();
        try {
            firstWindow.findElement(By.id("invisible")).getText();
            fail("Invisible element should not be accessible");
        } catch (NoSuchElementException ex) {
        }
        assertFalse(Boolean.parseBoolean(principalName));
        assertFalse(Boolean.parseBoolean(isUserInRole));
        assertFalse(Boolean.parseBoolean(isUserInSomeRole));
        assertTrue(Boolean.parseBoolean(currentRequestNotNull));
        assertFalse(Boolean.parseBoolean(isUserSignedIn));
        assertThat(disabled, is("DisabledLabel"));
    }

    @Test
    public void viewConventions() throws IOException {
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(ConventionalView.CONSTRUCT_COUNT), is(0));
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class) +
                ParameterizedNavigationUI.getNavigateToParam("conventional"));
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "conventional");
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(ConventionalView.CONSTRUCT_COUNT), is(1));
        assertDefaultRootNotInstantiated();

    }

    @Test
    public void subView() throws MalformedURLException {
        openWindow(deriveMappingForUI(NavigatableUI.class) + "#!main");
        waitForValue(LABEL, MainView.VIEW_ID);
        openWindow(deriveMappingForUI(NavigatableUI.class) + "#!main/subview");
        waitForValue(LABEL, SubView.VIEW_ID);
    }
}
