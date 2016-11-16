package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.cdi.uis.AnotherPathCollisionUI;
import com.vaadin.cdi.uis.PathCollisionUI;
import com.vaadin.cdi.uis.RootUI;

public class CDIIntegrationWithConflictingUIPathTest extends
        AbstractCDIIntegrationTest {

    @Before
    public void resetCounter() {
        PathCollisionUI.resetCounter();
        AnotherPathCollisionUI.resetCounter();
    }

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
        assertThat(RootUI.getNumberOfInstances(), is(0));
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
