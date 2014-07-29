package com.vaadin.cdi;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractManagedCDIIntegrationTest extends
        AbstractCDIIntegrationTest {

    @ArquillianResource
    URL contextPath;

    public void openWindow(String uri) throws MalformedURLException {
        openWindow(firstWindow, uri);
    }

    public void openWindow(WebDriver window, String uri)
            throws MalformedURLException {
        openWindowNoWait(window, uri, contextPath);
        (new WebDriverWait(window, 15)).until(ExpectedConditions
                .presenceOfElementLocated(LABEL));
    }

    public void openWindowNoWait(String uri) throws MalformedURLException {
        openWindowNoWait(firstWindow, uri, contextPath);
    }

}
