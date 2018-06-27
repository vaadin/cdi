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

import com.vaadin.cdi.itest.smoke.CdiView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("ArquillianTooManyDeployment")
public class SmokeTest extends AbstractCdiTest {

    @Deployment(name = "noncdi", testable = false)
    public static WebArchive createCdiServletDisabledDeployment() {
        return ArchiveProvider.createWebArchive("noncdi-test",
                CdiView.class)
                .addAsWebInfResource(ArchiveProvider.class.getClassLoader()
                        .getResource("disablecdi-web.xml"), "web.xml");
    }

    @Deployment(name = "cdi", testable = false)
    public static WebArchive createCdiServletEnabledDeployment() {
        return ArchiveProvider.createWebArchive("cdi-test",
                CdiView.class);
    }

    @Before
    public void setUp() {
        open();
        $("button").first().click();
    }

    @Test
    @OperateOnDeployment("noncdi")
    public void injectionDoesNotHappenWithDisabledCdiServlet() {
        assertLabelEquals("no CDI");
    }

    @Test
    @OperateOnDeployment("cdi")
    public void injectionHappensWithEnabledCdiServlet() {
        assertLabelEquals("hello CDI");
    }

    private void assertLabelEquals(String expected) {
        Assert.assertEquals(expected, $("label").first().getText());
    }

}
