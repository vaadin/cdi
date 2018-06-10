/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.template.TestTemplate;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TemplateTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("templates",
                archive -> archive
                        .addClasses(TestTemplate.class)
                        .addAsWebResource(
                                "frontend/test-template.html",
                                "frontend/test-template.html")
        );
    }

    @Before
    public void setUp() {
        open();
    }

    @Test
    public void testScopedComponentInjectedToTemplate() {
        WebElement shadowRootOwner = findElement(By.tagName("test-template"));
        WebElement label = getInShadowRoot(shadowRootOwner, By.id("label"));
        Assert.assertEquals("", label.getText());
        getInShadowRoot(shadowRootOwner, By.id("input")).sendKeys("CDI\t");
        Assert.assertEquals("CDI", label.getText());
    }
}
