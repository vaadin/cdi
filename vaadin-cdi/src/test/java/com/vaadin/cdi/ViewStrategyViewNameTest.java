/*
 * Copyright 2000-2013 Vaadin Ltd.
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
 *
 */

package com.vaadin.cdi;

import com.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

public class ViewStrategyViewNameTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyViewName", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAME + "/p1",
                ViewStrategyUI.BYVIEWNAME + "/p1",
                ",byviewname:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAME,
                ViewStrategyUI.BYVIEWNAME,
                ",byviewname:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersHoldsContext() throws Exception {
        navigateTo(ViewStrategyUI.BYVIEWNAME + "/p1");
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertBeanValue(",byviewname:p1");

        navigateTo(ViewStrategyUI.BYVIEWNAME + "/p2");
        // context hold
        assertConstructCounts(1);
        assertDestroyCounts(0);
        // navigation happened - init called again
        assertBeanValue(",byviewname:p1,byviewname:p2");
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertToOtherViewContextCreated(
                ViewStrategyUI.BYVIEWNAME,
                ",byviewname:"
        );
    }

    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByViewNameView.DESTROY_COUNT;
    }

    protected String getTestedViewConstructCounter() {
        return ViewStrategyUI.ByViewNameView.CONSTRUCT_COUNT;
    }

}
