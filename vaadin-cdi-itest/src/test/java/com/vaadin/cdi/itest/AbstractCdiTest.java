/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import com.vaadin.flow.testutil.ChromeBrowserTest;
import com.vaadin.testbench.parallel.Browser;

@RunWith(Arquillian.class)
@RunAsClient
abstract public class AbstractCdiTest extends ChromeBrowserTest {

    @ArquillianResource
    protected URL deploymentUrl;

    @Override
    protected String getRootURL() {
        return super.getRootURL() + deploymentUrl.getPath();
    }

    @Override
    protected int getDeploymentPort() {
        return deploymentUrl.getPort();
    }

    @Override
    protected String getTestPath() {
        return "/";
    }

    @Override
    public void setup() throws Exception {
        setDesiredCapabilities(Browser.CHROME.getDesiredCapabilities());
        super.setup();
    }

    protected void click(String elementId) {
        findElement(By.id(elementId)).click();
    }

    protected void follow(String linkText) {
        findElement(By.linkText(linkText)).click();
    }

    protected String getText(String id) {
        return findElement(By.id(id)).getText();
    }

    protected void assertCountEquals(int expectedCount, String counter)
            throws IOException {
        Assert.assertEquals(expectedCount, getCount(counter));
    }

    protected void assertTextEquals(String expectedText, String elementId) {
        Assert.assertEquals(expectedText, getText(elementId));
    }

    protected void resetCounts() throws IOException {
        slurp("?resetCounts");
    }

    protected int getCount(String id) throws IOException {
        getCommandExecutor().waitForVaadin();
        String line = slurp("?getCount=" + id);
        return Integer.parseInt(line);
    }

    private String slurp(String uri) throws IOException {
        URL url = new URL(getRootURL() + uri);
        InputStream is = url.openConnection().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        reader.close();
        return line;
    }
}
