/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import java.io.File;
import java.lang.annotation.Annotation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.cdi.itest.push.PushComponent;

public class PushTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("push", webArchive -> webArchive
                .addPackage(PushComponent.class.getPackage())
                .addAsResource(new File("target/classes/META-INF")));
    }

    @Test
    public void wsWithXhrBackgroundRequestAndSessionDoesNotActive() {
        getDriver().get(getTestURL() + "websocket-xhr");
        click(PushComponent.RUN_BACKGROUND);
        waitForPush();
        assertAllExceptRequestAndSessionActive();
    }

    @Test
    public void wsWithXhrForegroundAllContextsActive() {
        getDriver().get(getTestURL() + "websocket-xhr");
        click(PushComponent.RUN_FOREGROUND);
        assertContextActive(RequestScoped.class, true);
        assertContextActive(SessionScoped.class, true);
        assertContextActive(ApplicationScoped.class, true);
        assertVaadinContextsActive();
    }

    @Test
    public void wsNoXhrBackgroundRequestAndSessionDoesNotActive() {
        getDriver().get(getTestURL() + "websocket");
        click(PushComponent.RUN_BACKGROUND);
        waitForPush();
        assertAllExceptRequestAndSessionActive();
    }

    @Test
    public void wsNoXhrForegroundRequestAndSessionDoesNotActive() {
        getDriver().get(getTestURL() + "websocket");
        click(PushComponent.RUN_FOREGROUND);
        assertAllExceptRequestAndSessionActive();
    }

    private void assertAllExceptRequestAndSessionActive() {
        assertContextActive(RequestScoped.class, false);
        assertContextActive(SessionScoped.class, false);
        assertContextActive(ApplicationScoped.class, true);
        assertVaadinContextsActive();
    }

    private void assertVaadinContextsActive() {
        assertContextActive(RouteScoped.class, true);
        assertContextActive(UIScoped.class, true);
        assertContextActive(VaadinSessionScoped.class, true);
        assertContextActive(VaadinServiceScoped.class, true);
    }

    private void assertContextActive(Class<? extends Annotation> scope,
                                     boolean active) {
        assertTextEquals(active + "", scope.getName());
    }

    private void waitForPush() {
        waitForElementPresent(By.id(RequestScoped.class.getName()));
    }
}
