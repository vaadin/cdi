/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@RunWith(Arquillian.class)
@RunAsClient
abstract public class AbstractCDIIntegrationTest {

    @Drone
    WebDriver firstWindow;

    @ArquillianResource
    Deployer deployer;
    
    protected static final By LABEL = By.id("label");
    public static final By VIEW_LABEL = By.id("view");
    protected static final By BUTTON = By.id("button");
    protected static final By NAVIGATE_BUTTON = By.id("navigate");
    protected static final String INSTRUMENTED_UI_URI = "instrumentedUI";
    private static final String SECOND_UI_URI = "secondUI";
    protected static final String DANGLING_VIEW_URI = SECOND_UI_URI
                + "/#!danglingView";

    public void openWindowNoWait(WebDriver window, String uri, URL contextPath)
            throws MalformedURLException {
        URL url = new URL(contextPath.toString() + uri);
        window.navigate().to(url);
    }

    public int number(String txt) {
        return Integer.parseInt(txt);
    }
    
    public WebElement findElement(By by) {
        return firstWindow.findElement(by);
    }
    
    public WebElement findElement(String id) {
        return findElement(By.id(id));
    }
    public void clickAndWait(String id) {
        findElement(id).click();
        waitForClient();
    }

    public void clickAndWait(By by) {
        findElement(by).click();
        waitForClient();
    }

    public void waitForClient() {
        new WebDriverWait(firstWindow, Duration.ofSeconds(10)).until(input ->
                (Boolean) ((JavascriptExecutor) firstWindow)
                        .executeScript("return !vaadin.clients[Object.keys(vaadin.clients)[0]].isActive()"));
    }

    public void refreshWindow() {
        refreshWindow(firstWindow);
    }

    public void refreshWindow(WebDriver window) {
        window.navigate().refresh();
        (new WebDriverWait(window, Duration.ofSeconds(15))).until(ExpectedConditions
                .presenceOfElementLocated(LABEL));
    }

}
