package com.vaadin.cdi;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import com.vaadin.cdi.internal.MyLabel;

public class ComponentProxiesPreventDeployment extends
        AbstractCDIIntegrationTest {

    @Deployment(name = "componentProxies", managed = false)
    public static WebArchive componentProxies() {
        return ArchiveProvider.createWebArchive("componentProxies",
                MyLabel.class);
    }

    @Test
    public void componentInANormalScopeDoesntDeploy()
            throws MalformedURLException {
        try {
            System.out.println("DEPLOYING");
            // Deployment doesn't declare that it can throw DeploymentException
            // De-facto it can
            deployer.deploy("componentProxies");
            System.out.println("Deployed");
            fail("A Vaadin Component in a NormalScope should not deploy.");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
            System.out.println("Exiting try block");
        }
    }

}
