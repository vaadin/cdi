package com.vaadin.cdi;

import com.vaadin.cdi.views.CDIViewNotImplementingView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

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
    @Test(expected = Exception.class)
    @InSequence(-2)
    public void cdiViewWithoutViewBreaksDeployment() throws Exception {
        deployer.deploy("cdiViewWithoutView");
        fail("CDIView that does not implement View should not be deployable");
    }

}
