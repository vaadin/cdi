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
 */

package com.vaadin.cdi;

import com.vaadin.cdi.uis.CustomMappingUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CDIIntegrationWithCustomDeploymentTest extends
        AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment(name = "customURIMapping")
    public static WebArchive archiveWithCustomURIMapping() {
        return ArchiveProvider
                .createWebArchive("custom", CustomMappingUI.class);
    }

    @Test
    @OperateOnDeployment("customURIMapping")
    public void customServletMapping() throws IOException {
        assertThat(getCount(CustomMappingUI.CONSTRUCT_COUNT), is(0));
        openWindow("customURI/");
        assertThat(getCount(CustomMappingUI.CONSTRUCT_COUNT), is(1));
    }
}
