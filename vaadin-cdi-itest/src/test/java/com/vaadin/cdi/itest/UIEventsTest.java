/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.uievents.NavigationObserver;
import com.vaadin.cdi.itest.uievents.UIEventsView;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.testbench.TestBenchElement;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.List;

public class UIEventsTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("uievents",
                UIEventsView.class, NavigationObserver.class);
    }

    @Before
    public void setUp() {
        getDriver().get(getRootURL() + "uievents");
    }

    @Test
    public void navigationEventsObserved() {
        List<TestBenchElement> events = $("div")
                .id(UIEventsView.NAVIGATION_EVENTS)
                .$("label").all();
        Assert.assertEquals(3, events.size());
        assertEventIs(events.get(0), BeforeLeaveEvent.class);
        assertEventIs(events.get(1), BeforeEnterEvent.class);
        assertEventIs(events.get(2), AfterNavigationEvent.class);
    }

    @Test
    public void pollEventObserved() {
        waitForElementPresent(By.id(UIEventsView.POLL_FROM_CLIENT));
        assertTextEquals("true", UIEventsView.POLL_FROM_CLIENT);
    }

    private void assertEventIs(TestBenchElement eventElem, Class<?> eventClass) {
        Assert.assertEquals(eventClass.getSimpleName(), eventElem.getText());
    }

}
