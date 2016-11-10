package com.vaadin.cdi;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.enterprise.inject.New;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(Arquillian.class)
@RunAsClient
abstract public class AbstractCDIIntegrationTest {

    @Drone
    @New
    WebDriver firstWindow;

    @ArquillianResource
    Deployer deployer;
    
    protected static final By LABEL = By.id("label");
    public static final By VIEW_LABEL = By.id("view");
    protected static final By BUTTON = By.id("button");
    protected static final By NAVIGATE_BUTTON = By.id("navigate");
    protected static final String INSTRUMENTED_UI_URI = "instrumentedUI";
    private static final String SECOND_UI_URI = "secondUI";
    protected static final String INSTRUMENTED_VIEW_URI = INSTRUMENTED_UI_URI
                + "/#!instrumentedView";
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

}
