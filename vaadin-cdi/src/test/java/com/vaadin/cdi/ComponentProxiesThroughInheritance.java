package com.vaadin.cdi;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import com.vaadin.cdi.internal.MyBean;
import com.vaadin.cdi.internal.MyComponent;

public class ComponentProxiesThroughInheritance extends AbstractCDIIntegrationTest {
    
    @Deployment(name = "inheritedScopeComponentProxies", managed = false)
    public static WebArchive inheritedScopeComponentProxies() {
        return ArchiveProvider.createWebArchive(
                "inheritedScopeComponentProxies", MyComponent.class,
                MyBean.class);
    }
    
    @Test
    public void componentInheritingNormalScopeDoesntDeploy()
            throws MalformedURLException {
        try {
            System.out.println("DEPLOYING");
            // Deployment doesn't declare that it can throw DeploymentException
            // De-facto it can
            deployer.deploy("inheritedScopeComponentProxies");
            System.out.println("Deployed");
            fail("A Vaadin Component in a NormalScope should not deploy.");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
            System.out.println("Exiting try block");
        }
    }
}
