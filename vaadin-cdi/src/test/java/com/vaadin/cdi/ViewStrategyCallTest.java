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
 *
 */

package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.ViewStrategyCallUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;

import static com.vaadin.cdi.uis.ViewStrategyCallUI.*;
import static org.junit.Assert.assertEquals;

public class ViewStrategyCallTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyCall", ViewStrategyCallUI.class);
    }

    @Before
    public void setUp() throws MalformedURLException {
        String uri = Conventions.deriveMappingForUI(ViewStrategyCallUI.class);
        openWindow(uri);
    }

    @Test
    public void testStrategyNotCalledWithoutViewContext() throws Exception {
        // No need call navigateTo. First navigation already done
        // to root view during initialization of UI.
        assertStrategyNotCalled();
    }

    @Test
    public void testRootViewWithParams() throws Exception {
        navigateTo("/p1/p2");
        assertStrategyCalled("", "p1/p2");
    }

    @Test
    public void testRootViewNoParams() throws Exception {
        navigateTo("");
        assertStrategyCalled("", "");
    }

    @Test
    public void testSameViewNoParams() throws Exception {
        navigateTo(SAMEVIEW);
        assertStrategyCalled(SAMEVIEW, "");
    }

    @Test
    public void testSameViewWithParams() throws Exception {
        navigateTo(SAMEVIEW + "/p1/p2");
        assertStrategyCalled(SAMEVIEW, "p1/p2");
    }

    @Test
    public void testReturnTrueHoldContext() throws Exception {
        navigateTo(SAMEVIEW);
        // contextStrategy for root view returns true
        String value = findElement(BEANVALUE_OUTPUT_ID).getText();
        assertEquals(BEANVALUE, value);
    }

    @Test
    public void testReturnFalseReleaseContext() throws Exception {
        navigateTo(SAMEVIEW);
        // Strategy of active context called.
        // It is still the context opened on root view.
        navigateTo(OTHERVIEW);
        // strategy returns false for 'other'
        String value = findElement(BEANVALUE_OUTPUT_ID).getText();
        assertEquals(UNDEFINED, value);
    }

    private void navigateTo(String viewState) {
        findElement(TARGETSTATE_ID).clear();
        findElement(TARGETSTATE_ID).sendKeys(viewState);
        clickAndWait(NAVBTN_ID);
    }

    private void assertStrategyNotCalled() {
        assertStrategyCalled(UNDEFINED, UNDEFINED);
    }

    private void assertStrategyCalled(String expectedViewName, String expectedParameters) {
        String viewName = findElement(VIEWNAME_OUTPUT_ID).getText();
        String parameters = findElement(PARAMETERS_OUTPUT_ID).getText();
        assertEquals(expectedViewName, viewName);
        assertEquals(expectedParameters, parameters);
    }
}
