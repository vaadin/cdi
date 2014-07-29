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

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Predicate;
import com.vaadin.cdi.uis.Boundary;
import com.vaadin.cdi.uis.DanglingView;
import com.vaadin.cdi.uis.DependentCDIEventListener;
import com.vaadin.cdi.uis.EnterpriseUI;
import com.vaadin.cdi.uis.InstrumentedInterceptor;
import com.vaadin.cdi.uis.InstrumentedUI;
import com.vaadin.cdi.uis.InstrumentedView;
import com.vaadin.cdi.uis.InterceptedBean;
import com.vaadin.cdi.uis.InterceptedUI;
import com.vaadin.cdi.uis.NoViewProviderNavigationUI;
import com.vaadin.cdi.uis.ParameterizedNavigationUI;
import com.vaadin.cdi.uis.PlainAlternativeUI;
import com.vaadin.cdi.uis.PlainUI;
import com.vaadin.cdi.uis.RestrictedView;
import com.vaadin.cdi.uis.RootUI;
import com.vaadin.cdi.uis.ScopedInstrumentedView;
import com.vaadin.cdi.uis.SecondUI;
import com.vaadin.cdi.uis.SubUI;
import com.vaadin.cdi.uis.UIWithCDIDependentListener;
import com.vaadin.cdi.uis.UIWithCDISelfListener;
import com.vaadin.cdi.uis.UnsecuredUI;
import com.vaadin.cdi.uis.ViewWithoutAnnotation;
import com.vaadin.cdi.uis.WithAnnotationRegisteredView;

public class CDIIntegrationWithDefaultDeployment extends
        AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() {
        PlainUI.resetCounter();
        PlainAlternativeUI.resetCounter();
        InstrumentedUI.resetCounter();
        InstrumentedView.resetCounter();
        ScopedInstrumentedView.resetCounter();
        ViewWithoutAnnotation.resetCounter();
        WithAnnotationRegisteredView.resetCounter();
        SecondUI.resetCounter();
        RootUI.resetCounter();
        UIWithCDIDependentListener.resetCounter();
        UIWithCDISelfListener.resetCounter();
        DependentCDIEventListener.resetCounter();
        DependentCDIEventListener.resetEventCounter();
        ParameterizedNavigationUI.reset();
        NoViewProviderNavigationUI.resetCounter();
        InterceptedUI.resetCounter();
        EnterpriseUI.resetCounter();

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
                Boundary.class, SubUI.class, PlainAlternativeUI.class,
                NoViewProviderNavigationUI.class);
    }

    @Test
    public void browserRestartCreatesNewInstance() throws MalformedURLException {
        String uri = deriveMappingForUI(PlainUI.class);
        openWindow(uri);

        // Throws exception if element not found
        firstWindow.findElement(LABEL);

        assertThat(PlainUI.getNumberOfInstances(), is(1));

        // reset session
        openWindow(uri);

        // Throws exception if element not found
        firstWindow.findElement(LABEL);

        assertThat(PlainUI.getNumberOfInstances(), is(2));
        assertDefaultRootNotInstantiated();

    }

    @Test
    public void oneToOneRelationBetweenBrowserAndUI()
            throws MalformedURLException {

        openWindow(INSTRUMENTED_UI_URI);

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 1);
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 2);
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));

        openWindow(INSTRUMENTED_UI_URI);

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 1);
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));

        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, 2);
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));
        assertDefaultRootNotInstantiated();
    }

    private void waitForValue(final By by, final int value) {
        Graphene.waitModel(firstWindow).withTimeout(10, TimeUnit.SECONDS)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return number(driver.findElement(by).getText()) == value;
                    }
                });
    }

    private void waitForValue(final By by, final String value) {
        Graphene.waitModel(firstWindow).withTimeout(10, TimeUnit.SECONDS)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return value.equals(driver.findElement(by).getText());
                    }
                });
    }

    @Test
    public void dependentScopedViewIsInstantiatedTwiceWithViewProvider()
            throws MalformedURLException {
        openWindow(firstWindow, INSTRUMENTED_VIEW_URI);
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "ViewLabel");
        assertThat(InstrumentedView.getNumberOfInstances(), is(2));
    }

    @Test
    public void dependentScopedViewIsInstantiatedOnce()
            throws MalformedURLException {
        String uri = deriveMappingForUI(NoViewProviderNavigationUI.class);
        openWindow(uri);
        assertThat(InstrumentedView.getNumberOfInstances(), is(1));
        firstWindow.findElement(NAVIGATE_BUTTON).click();

        waitForValue(VIEW_LABEL, "ViewLabel");
        assertThat(InstrumentedView.getNumberOfInstances(), is(1));
        assertThat(NoViewProviderNavigationUI.getNumberOfInstances(), is(1));
        assertThat(NoViewProviderNavigationUI.getNumberOfNavigations(), is(1));

        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "ViewLabel");
        assertThat(InstrumentedView.getNumberOfInstances(), is(1));
        assertThat(NoViewProviderNavigationUI.getNumberOfInstances(), is(1));
        assertThat(NoViewProviderNavigationUI.getNumberOfNavigations(), is(2));

    }

    @Test
    public void rootUIDiscovery() throws MalformedURLException {
        assertThat(RootUI.getNumberOfInstances(), is(0));
        openWindow("");
        assertThat(RootUI.getNumberOfInstances(), is(1));
    }

    @Test
    public void uiInheritance() throws MalformedURLException {
        openWindow(deriveMappingForUI(SubUI.class));
        assertThat(SubUI.getNumberOfInstances(), is(1));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void refreshButtonCreatesNewUIInstance()
            throws MalformedURLException {
        openWindow(INSTRUMENTED_UI_URI);
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));
        firstWindow.navigate().refresh();
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));
        assertDefaultRootNotInstantiated();
    }

    @Test
    public void danglingViewCauses404() throws MalformedURLException {
        openWindow(DANGLING_VIEW_URI);
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        assertThat(SecondUI.getNumberOfInstances(), is(1));
        assertThat(DanglingView.getNumberOfInstances(), is(0));
    }

    @Test
    public void alternativeIsNotAccessible() throws MalformedURLException {
        openWindowNoWait(deriveMappingForUI(PlainAlternativeUI.class));
        final String expectedErrorMessage = firstWindow.getPageSource();
        assertThat(expectedErrorMessage, containsString("404"));
        assertThat(PlainAlternativeUI.getNumberOfInstances(), is(0));
    }

    @Test
    public void cdiEventsArrivesInTheSameUIScopedInstance()
            throws MalformedURLException {
        assertThat(UIWithCDISelfListener.getNumberOfInstances(), is(0));
        assertThat(UIWithCDISelfListener.getNumberOfDeliveredEvents(), is(0));
        String uri = deriveMappingForUI(UIWithCDISelfListener.class);
        openWindow(uri);
        firstWindow.findElement(BUTTON).click();
        assertThat(UIWithCDISelfListener.getNumberOfInstances(), is(1));
        assertThat(UIWithCDISelfListener.getNumberOfDeliveredEvents(), is(1));
        firstWindow.findElement(BUTTON).click();
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
        String uri = deriveMappingForUI(UIWithCDIDependentListener.class);
        openWindow(uri);
        firstWindow.findElement(BUTTON).click();
        assertThat(UIWithCDIDependentListener.getNumberOfInstances(), is(1));
        assertThat(DependentCDIEventListener.getNumberOfInstances(), is(1));
        assertThat(DependentCDIEventListener.getNumberOfDeliveredEvents(),
                is(1));
        firstWindow.findElement(BUTTON).click();
        assertThat(UIWithCDIDependentListener.getNumberOfInstances(), is(1));
        assertThat(DependentCDIEventListener.getNumberOfInstances(), is(2));
        assertThat(DependentCDIEventListener.getNumberOfDeliveredEvents(),
                is(2));

    }

    @Test
    public void interceptedScopedEventListener() throws MalformedURLException {
        assertThat(InterceptedUI.getNumberOfInstances(), is(0));
        assertThat(InstrumentedInterceptor.getCounter(), is(0));
        String uri = deriveMappingForUI(InterceptedUI.class);
        openWindow(uri);
        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, "hello from intercepted bean");
        assertThat(InstrumentedInterceptor.getCounter(), is(1));
        firstWindow.findElement(BUTTON).click();
        assertThat(InstrumentedInterceptor.getCounter(), is(2));

    }

    @Test
    public void navigationToRestrictedViewFails() throws MalformedURLException {
        assertThat(ParameterizedNavigationUI.getNumberOfInstances(), is(0));
        assertThat(RestrictedView.getNumberOfInstances(), is(0));
        ParameterizedNavigationUI.NAVIGATE_TO = "restrictedView";
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class));
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        assertThat(ParameterizedNavigationUI.getNumberOfInstances(), is(1));
        assertThat(RestrictedView.getNumberOfInstances(), is(0));
        assertDefaultRootNotInstantiated();

    }

    @Test
    public void ejbInvocation() throws MalformedURLException {
        openWindow(deriveMappingForUI(EnterpriseUI.class));
        assertThat(EnterpriseUI.getNumberOfInstances(), is(1));
        firstWindow.findElement(BUTTON).click();
        waitForValue(LABEL, "Echo: 1");
        final String labelText = firstWindow.findElement(LABEL).getText();
        assertThat(labelText, startsWith("Echo:"));
        assertThat(EnterpriseUI.getNumberOfInstances(), is(1));
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

}
