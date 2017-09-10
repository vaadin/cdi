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
import com.vaadin.cdi.uis.ViewStrategyInitUI;
import com.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ViewStrategiesUiInitTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategiesUiInit", ViewStrategyInitUI.class);
    }

    @Before
    public void setUp() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(ViewStrategyUI.class)+"#!home/p1";
        openWindow(viewUri);
    }

    @Test
    public void testViewNameStrategyUpAfterUiInit() throws Exception {
        clickAndWait(ViewStrategyInitUI.VIEWNAME_BTN_ID);
        final String result = findElement(ViewStrategyInitUI.OUTPUT_ID).getText();
        Assert.assertEquals("true", result);
    }

    @Test
    public void testViewNameAndParametersStrategyUpAfterUiInit() throws Exception {
        clickAndWait(ViewStrategyInitUI.VIEWNAMEPARAMS_BTN_ID);
        final String result = findElement(ViewStrategyInitUI.OUTPUT_ID).getText();
        Assert.assertEquals("true", result);
    }
}
