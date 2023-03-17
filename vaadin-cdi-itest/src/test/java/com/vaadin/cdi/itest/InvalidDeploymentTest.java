/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.invaliddeployment.DeploymentView;
import com.vaadin.cdi.itest.invaliddeployment.NormalScopedLabel;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class InvalidDeploymentTest {

    @ArquillianResource
    private Deployer deployer;

    @Deployment(name = "invalid-deployment", testable = false, managed = false)
    public static WebArchive invalidDeployment() {
        return ArchiveProvider.createWebArchive("invalid-deployment",
                DeploymentView.class, NormalScopedLabel.class);
    }

    @Test(expected = Exception.class)
    public void invalidDeploymentShouldBreakDeploy() {
        deployer.deploy("invalid-deployment");
    }

}
