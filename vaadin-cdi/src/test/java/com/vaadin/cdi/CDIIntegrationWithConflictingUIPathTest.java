package com.vaadin.cdi;

import com.vaadin.cdi.uis.AnotherPathCollisionUI;
import com.vaadin.cdi.uis.PathCollisionUI;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.fail;

public class CDIIntegrationWithConflictingUIPathTest extends
        AbstractCDIIntegrationTest {


    @Deployment(name = "uiPathCollision", managed = false)
    public static WebArchive multipleUIsWithSamePath() {
        return ArchiveProvider.createWebArchive("uiPathCollision",
                PathCollisionUI.class, AnotherPathCollisionUI.class);
    }

    /**
     * Tests invalid deployment of multiple roots within a WAR Should be started
     * first -- Arquillian deployments are not perfectly isolated.
     */
    @Test
    @InSequence(-2)
    public void uiPathCollisionBreaksDeployment() throws MalformedURLException {
        try {
            System.out.println("DEPLOYING");
            // Deployment doesn't declare that it can throw DeploymentException
            // De-facto it can
            deployer.deploy("uiPathCollision");
            System.out.println("Deployed");
            fail("Duplicate deployment paths should not be deployable");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
            System.out.println("Exiting try block");
        }
    }

}
