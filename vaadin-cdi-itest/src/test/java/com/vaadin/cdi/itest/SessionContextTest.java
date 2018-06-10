/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.sessioncontext.SessionContextView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
    public void testUIsAccessSameSession() {
        assertLabelEquals("");
        click(SessionContextView.SETVALUEBTN_ID);
        getDriver().navigate().refresh();//creates new UI
        assertLabelEquals(SessionContextView.VALUE);
    }

    @Test
    public void testVaadinSessionCloseDestroysSessionContext() throws Exception {
        assertDestroyCountEquals(0);
        click(SessionContextView.INVALIDATEBTN_ID);
        waitForVaadin();
        assertDestroyCountEquals(1);
    }

    @Test
    public void testHttpSessionCloseDestroysSessionContext() throws Exception {
        assertDestroyCountEquals(0);
        click(SessionContextView.HTTP_INVALIDATEBTN_ID);
        waitForVaadin();
        assertDestroyCountEquals(1);
    }

    @Test
    @Ignore
    //ignored because it's slow, and expiration should be same as session close
    public void testHttpSessionExpirationDestroysSessionContext() throws Exception {
        assertDestroyCountEquals(0);
        click(SessionContextView.EXPIREBTN_ID);
        boolean destroyed = false;
        for (int i=0; i<60; i++) {
            Thread.sleep(1000);
            if (getCount(DESTROY_COUNT) > 0) {
                System.out.printf("session expired after %d seconds\n", i);
                destroyed = true;
                break;
            }
        }
        assertTrue(destroyed);
    }

    private void assertLabelEquals(String expected) {
        Assert.assertEquals(expected, $("label").first().getText());
    }

    private void assertDestroyCountEquals(int expectedCount) throws IOException {
        assertCountEquals(expectedCount, DESTROY_COUNT);
    }

}
