package com.vaadin.cdi;

import com.vaadin.cdi.uis.*;
import com.vaadin.cdi.views.CDIViewDependent;
import com.vaadin.cdi.views.CDIViewNotImplementingView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.junit.Assert.fail;

public class InappropriateDeploymentTest extends
        AbstractCDIIntegrationTest {

    @Deployment(name = "cdiViewWithoutView", managed = false, testable = false)
    public static WebArchive cdiViewWithoutView() {
        return ArchiveProvider.createWebArchive("cdiViewWithoutView",
                CDIViewNotImplementingView.class);
    }

    @Test(expected = Exception.class)
    public void cdiViewWithoutViewBreaksDeployment() throws Exception {
        deployer.deploy("cdiViewWithoutView");
        fail("CDIView that does not implement View should not be deployable");
    }

    @Deployment(name = "cdiViewDependent", managed = false, testable = false)
    public static WebArchive cdiViewDependent() {
        return ArchiveProvider.createWebArchive("cdiViewDependent",
                CDIViewDependent.class);
    }

    @Test(expected = Exception.class)
    public void cdiViewDependentBreaksDeployment() throws Exception {
        deployer.deploy("cdiViewDependent");
        fail("Dependent scoped CDIView should not be deployable");
    }

    @Deployment(name = "cdiUIWithoutUI", managed = false, testable = false)
    public static WebArchive cdiUIWithoutUI() {
        return ArchiveProvider.createWebArchive("cdiUIWithoutUI",
                CDIUINotExtendingUI.class);
    }

    @Test(expected = Exception.class)
    public void cdiUIWithoutUIBreaksDeployment() throws Exception {
        deployer.deploy("cdiUIWithoutUI");
        fail("CDIUI that does not extend UI should not be deployable");
    }

    @Deployment(name = "cdiUIWrongScope", managed = false, testable = false)
    public static WebArchive cdiUIWrongScope() {
        return ArchiveProvider.createWebArchive("cdiUIWrongScope",
                CDIUIWrongScope.class);
    }

    @Test(expected = Exception.class)
    public void cdiUIWrongScopeBreaksDeployment() throws Exception {
        deployer.deploy("cdiUIWrongScope");
        fail("CDIUI that is not @UIScoped should not be deployable");
    }

    @Deployment(name = "uiPathCollision", managed = false, testable = false)
    public static WebArchive uiPathCollision() {
        return ArchiveProvider.createWebArchive("uiPathCollision",
                PathCollisionUI.class, AnotherPathCollisionUI.class);
    }

    @Test(expected = Exception.class)
    public void uiPathCollisionBreaksDeployment() throws Exception {
        deployer.deploy("uiPathCollision");
        fail("Duplicate deployment paths should not be deployable");
    }

    @Deployment(name = "nestedServlet", managed = false, testable = false)
    public static WebArchive nestedServlet() {
        return ArchiveProvider.createWebArchive("nestedServlet",
                UIWithNestedServlet.class);
    }

    @Test(expected = Exception.class)
    public void nestedServletBreaksDeployment() throws Exception {
        deployer.deploy("nestedServlet");
        fail("Servlet class nested in the UI should not be deployable");
    }
}
