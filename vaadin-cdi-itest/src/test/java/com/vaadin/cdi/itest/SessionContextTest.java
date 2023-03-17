/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.sessioncontext.SessionContextView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.vaadin.cdi.itest.sessioncontext.SessionContextView.SessionScopedBean.DESTROY_COUNT;
import static org.junit.Assert.assertTrue;

public class SessionContextTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("session-context",
                SessionContextView.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        open();
    }

    @Test
    public void sameSessionIsAccessibleFromUIs() {
        assertLabelEquals("");
        click(SessionContextView.SETVALUEBTN_ID);
        getDriver().navigate().refresh();//creates new UI
        assertLabelEquals(SessionContextView.VALUE);
    }

    @Test
    public void vaadinSessionCloseDestroysSessionContext() throws Exception {
        assertDestroyCountEquals(0);
        click(SessionContextView.INVALIDATEBTN_ID);
        assertDestroyCountEquals(1);
    }

    @Test
    public void httpSessionCloseDestroysSessionContext() throws Exception {
        assertDestroyCountEquals(0);
        click(SessionContextView.HTTP_INVALIDATEBTN_ID);
        assertDestroyCountEquals(1);
    }

    @Test
    @Category(SlowTests.class)
    public void httpSessionExpirationDestroysSessionContext() throws Exception {
        assertDestroyCountEquals(0);
        click(SessionContextView.EXPIREBTN_ID);
        boolean destroyed = false;
        getLogger().info("Waiting for session expiration...");
        for (int i=0; i<60; i++) {
            Thread.sleep(1000);
            if (getCount(DESTROY_COUNT) > 0) {
                getLogger().info("session expired after {} seconds", i);
                destroyed = true;
                break;
            }
        }
        assertTrue(destroyed);
    }

    private void assertLabelEquals(String expected) {
        assertTextEquals(expected, SessionContextView.VALUELABEL_ID);
    }

    private void assertDestroyCountEquals(int expectedCount) throws IOException {
        assertCountEquals(expectedCount, DESTROY_COUNT);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(SessionContextTest.class);
    }

}
