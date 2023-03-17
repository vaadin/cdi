/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import static com.vaadin.cdi.itest.service.ServiceView.ACTION;
import static com.vaadin.cdi.itest.service.ServiceView.EXPIRE;
import static com.vaadin.cdi.itest.service.ServiceView.FAIL;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.cdi.itest.service.BootstrapCustomizeView;
import com.vaadin.cdi.itest.service.BootstrapCustomizer;
import com.vaadin.cdi.itest.service.EventObserver;
import com.vaadin.cdi.itest.service.ServiceView;
import com.vaadin.cdi.itest.service.TestErrorHandler;
import com.vaadin.cdi.itest.service.TestSystemMessagesProvider;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.UIInitEvent;

public class ServiceTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider
                .createWebArchive("services", BootstrapCustomizer.class,
                        BootstrapCustomizeView.class, ServiceView.class,
                        EventObserver.class, TestErrorHandler.class,
                        TestSystemMessagesProvider.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
    }

    @Test
    public void bootstrapCustomizedByServiceInitEventObserver() {
        getDriver().get(getTestURL() + "bootstrap");
        assertTextEquals(BootstrapCustomizer.APPENDED_TXT,
                BootstrapCustomizer.APPENDED_ID);
    }

    @Test
    public void sessionExpiredMessageCustomized() {
        open();
        click(EXPIRE);
        click(ACTION);
        assertSystemMessageEquals(TestSystemMessagesProvider.EXPIRED_BY_TEST);
    }

    @Test
    public void errorHandlerCustomized() throws IOException {
        String counter = TestErrorHandler.class.getSimpleName();
        assertCountEquals(0, counter);
        open();
        click(FAIL);
        assertCountEquals(1, counter);
    }

    @Test
    public void sessionInitEventObserved() throws IOException {
        String initCounter = SessionInitEvent.class.getSimpleName();
        assertCountEquals(0, initCounter);
        getDriver().manage().deleteAllCookies();
        open();
        assertCountEquals(1, initCounter);
    }

    @Test
    public void sessionDestroyEventObserved() throws IOException {
        String destroyCounter = SessionDestroyEvent.class.getSimpleName();
        assertCountEquals(0, destroyCounter);
        open();
        assertCountEquals(0, destroyCounter);
        click(EXPIRE);
        assertCountEquals(1, destroyCounter);
    }

    @Test
    public void uiInitEventObserved() throws IOException {
        String uiInitCounter = UIInitEvent.class.getSimpleName();
        assertCountEquals(0, uiInitCounter);
        open();
        assertCountEquals(1, uiInitCounter);
    }

    private void assertSystemMessageEquals(String expected) {
        WebElement message = findElement(
                By.cssSelector("div.v-system-error div.message"));
        Assert.assertEquals(expected, message.getText());
    }
}
