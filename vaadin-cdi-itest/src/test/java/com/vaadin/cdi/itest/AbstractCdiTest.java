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


import com.vaadin.flow.testutil.ChromeBrowserTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.runner.RunWith;

import java.net.URL;

@RunWith(Arquillian.class)
@RunAsClient
abstract public class AbstractCdiTest extends ChromeBrowserTest {

    @ArquillianResource
    protected URL deploymentUrl;

    @Override
    protected String getRootURL() {
        return super.getRootURL() + deploymentUrl.getPath();
    }

    @Override
    protected int getDeploymentPort() {
        return deploymentUrl.getPort();
    }

    @Override
    protected String getTestPath() {
        return "";
    }
}
