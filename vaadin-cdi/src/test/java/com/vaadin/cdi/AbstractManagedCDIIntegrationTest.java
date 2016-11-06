package com.vaadin.cdi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.vaadin.cdi.internal.Conventions;

public abstract class AbstractManagedCDIIntegrationTest extends
        AbstractCDIIntegrationTest {

    @ArquillianResource
    URL contextPath;

    public void openWindow(Class uiClass) {
        try {
            openWindow(Conventions.deriveMappingForUI(uiClass));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to open UI " + uiClass.getCanonicalName(), e);
        }
    }

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

    public void refreshWindow() {
        refreshWindow(firstWindow);
    }

    public void refreshWindow(WebDriver window) {
        window.navigate().refresh();
        (new WebDriverWait(window, 15)).until(ExpectedConditions
                .presenceOfElementLocated(LABEL));
    }

    public void waitForValue(final By by, final int value) {
        Graphene.waitModel(firstWindow).withTimeout(10, TimeUnit.SECONDS)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return number(driver.findElement(by).getText()) == value;
                    }
                });
    }

    public void waitForValue(final By by, final String value) {
        Graphene.waitModel(firstWindow).withTimeout(10, TimeUnit.SECONDS)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return value.equals(driver.findElement(by).getText());
                    }
                });
    }

    public void clickAndWait(String id) {
        findElement(id).click();
        waitForClient();
    }

    public void waitForClient() {
        new WebDriverWait(firstWindow, 10).until(new ClientIsReadyPredicate());
    }

    private class ClientIsReadyPredicate implements Predicate<WebDriver> {
        @Override
        public boolean apply(WebDriver input) {
            return (Boolean) ((JavascriptExecutor) firstWindow)
                    .executeScript("return !vaadin.clients[Object.keys(vaadin.clients)[0]].isActive()");
        }
    }

}
