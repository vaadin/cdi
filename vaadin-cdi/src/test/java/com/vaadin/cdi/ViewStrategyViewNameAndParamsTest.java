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

public class ViewStrategyViewNameAndParamsTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyViewNameAndParams", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p1",
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p1",
                ",byviewnameparams:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.BYVIEWNAMEPARAMS,
                ViewStrategyUI.BYVIEWNAMEPARAMS,
                ",byviewnameparams:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersCreatesNewContext() throws Exception {
        assertAfterNavigateToTestedViewContextCreated(
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p1",
                ViewStrategyUI.BYVIEWNAMEPARAMS + "/p2",
                ",byviewnameparams:p1",
                ",byviewnameparams:p2"
        );
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertAfterNavigateToOtherViewContextCreated(
                ViewStrategyUI.BYVIEWNAMEPARAMS,
                ",byviewnameparams:"
        );
    }

    @Override
    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByViewNameAndParametersView.DESTROY_COUNT;
    }

    @Override
    protected String getTestedViewConstructCounter() {
        return ViewStrategyUI.ByViewNameAndParametersView.CONSTRUCT_COUNT;
    }
}
