package com.vaadin.cdi;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import com.vaadin.cdi.internal.MyStereotype;
import com.vaadin.cdi.internal.MyStereotypedComponent;

public class ComponentProxiesThroughStereotype extends AbstractCDIIntegrationTest {
    @Deployment(name = "stereotypedScopeProxies", managed = false)
    public static WebArchive stereotypedScopeProxies() {
        return ArchiveProvider.createWebArchive("stereotypedScopeProxies",
                MyStereotypedComponent.class, MyStereotype.class);
    }
    
    @Test
    public void componentWithStereotypeToNormalScopeDoesntDeploy()
            throws MalformedURLException {
        try {
            System.out.println("DEPLOYING");
            // Deployment doesn't declare that it can throw DeploymentException
            // De-facto it can
            deployer.deploy("stereotypedScopeProxies");
            System.out.println("Deployed");
            fail("A Vaadin Component in a NormalScope should not deploy.");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
            System.out.println("Exiting try block");
        }
    }
}
