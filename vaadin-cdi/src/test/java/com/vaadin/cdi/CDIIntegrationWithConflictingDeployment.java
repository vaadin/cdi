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

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import com.vaadin.cdi.uis.PlainColidingAlternativeUI;
import com.vaadin.cdi.uis.PlainUI;

public class CDIIntegrationWithConflictingDeployment extends
        AbstractManagedCDIIntegrationTest {

    @Deployment(name = "alternativeUiPathCollision")
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("alternativeUiPathCollision",
                PlainUI.class, PlainColidingAlternativeUI.class);
    }

    @Test
    @OperateOnDeployment("alternativeUiPathCollision")
    public void alternativeDoesNotColideWithPath() throws MalformedURLException {
        final String plainUIPath = deriveMappingForUI(PlainUI.class);
        final String plainAlternativeUI = deriveMappingForUI(PlainColidingAlternativeUI.class);
        assertThat(plainUIPath, is(plainAlternativeUI));
        openWindow(plainUIPath);
        assertThat(PlainUI.getNumberOfInstances(), is(1));
        assertThat(PlainColidingAlternativeUI.getNumberOfInstances(), is(0));
    }
}
