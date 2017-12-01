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

public class ViewStrategyAlwaysTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyAlways", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYALWAYS + "/p1",
                ViewStrategyUI.BYALWAYS + "/p1",
                ",byalways:p1",
                ",byalways:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYALWAYS,
                ViewStrategyUI.BYALWAYS,
                ",byalways:",
                ",byalways:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYALWAYS + "/p1",
                ViewStrategyUI.BYALWAYS + "/p2",
                ",byalways:p1",
                ",byalways:p2"
        );
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertAfterNavigateToOtherViewContextCreated(
                ViewStrategyUI.BYALWAYS,
                ",byalways:"
        );
    }

    @Override
    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByAlwaysView.DESTROY_COUNT;
    }

    @Override
    protected String getTestedViewConstructCounter() {
        return ViewStrategyUI.ByAlwaysView.CONSTRUCT_COUNT;
    }
}
