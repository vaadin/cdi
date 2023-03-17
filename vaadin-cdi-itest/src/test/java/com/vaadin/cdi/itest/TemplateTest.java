/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.cdi.itest.template.TestTemplate;
import com.vaadin.testbench.TestBenchElement;

public class TemplateTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("templates",
                TestTemplate.class);
    }

    @Before
    public void setUp() {
        open();
    }

    @Test
    public void scopedComponentInjectedToTemplate() {
        checkLogsForErrors();
        TestBenchElement template = $("test-template").first();
        TestBenchElement label = template.$(TestBenchElement.class).id("label");
        Assert.assertEquals("", label.getText());
        TestBenchElement input = template.$(TestBenchElement.class).id("input");
        input.sendKeys("CDI");
        input.sendKeys(Keys.ENTER);
        Assert.assertEquals("CDI", label.getText());
    }
}
