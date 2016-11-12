package com.vaadin.cdi;

import com.vaadin.cdi.views.CDIViewNotImplementingView;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.fail;

public class InappropriateCDIViewInDeploymentTest extends
        AbstractCDIIntegrationTest {

    @Deployment(name = "cdiViewWithoutView", managed = false, testable = false)
    public static WebArchive multipleUIsWithSamePath() {
        return ArchiveProvider.createWebArchive("cdiViewWithoutView",
                CDIViewNotImplementingView.class);
    }

    /**
     * Tests invalid deployment of multiple roots within a WAR Should be started
     * first -- Arquillian deployments are not perfectly isolated.
     */
    @Test
    @InSequence(-2)
    public void cdiViewWithoutViewBreaksDeployment()
            throws MalformedURLException {
        try {
            System.out.println("DEPLOYING");
            // Deployment doesn't declare that it can throw DeploymentException
            // De-facto it can
            deployer.deploy("cdiViewWithoutView");
            System.out.println("Deployed");
            fail("CDIView that does not implement View should not be deployable");
            throw new DeploymentException(null);
        } catch (DeploymentException e) {
            // Correct response
            System.out.println("Exiting try block");
        }
    }

}
