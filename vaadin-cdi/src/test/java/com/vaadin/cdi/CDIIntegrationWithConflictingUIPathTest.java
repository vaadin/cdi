package com.vaadin.cdi;

import com.vaadin.cdi.uis.AnotherPathCollisionUI;
import com.vaadin.cdi.uis.PathCollisionUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.junit.Assert.fail;

public class CDIIntegrationWithConflictingUIPathTest extends
        AbstractCDIIntegrationTest {


    @Deployment(name = "uiPathCollision", managed = false, testable = false)
    public static WebArchive multipleUIsWithSamePath() {
        return ArchiveProvider.createWebArchive("uiPathCollision",
                PathCollisionUI.class, AnotherPathCollisionUI.class);
    }

    /**
     * Tests invalid deployment of multiple roots within a WAR Should be started
     * first -- Arquillian deployments are not perfectly isolated.
     */
    @Test(expected = Exception.class)
    @InSequence(-2)
    public void uiPathCollisionBreaksDeployment() throws Exception {
        deployer.deploy("uiPathCollision");
        fail("Duplicate deployment paths should not be deployable");
    }

}
