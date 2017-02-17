package com.vaadin.cdi.shiro;

import com.vaadin.cdi.AbstractManagedCDIIntegrationTest;
import com.vaadin.cdi.ArchiveProvider;
import com.vaadin.cdi.uis.NavigatableUI;
import com.vaadin.cdi.views.AbstractNavigatableView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Simple test of Shiro access control.
 */
public class ShiroTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "shiro", testable = false)
    public static WebArchive initAndPostConstructAreConsistent() {
        PomEquippedResolveStage pom = Maven.resolver()
                .loadPomFromFile("pom.xml");
        return ArchiveProvider
                .createWebArchive("shiro", false, NavigatableUI.class,
                        ShiroAccessControl.class, ShiroWebListener.class,
                        ShiroWebFilter.class, AbstractNavigatableView.class,
                        AbstractShiroTestView.class, LoginPane.class,
                        GuestView.class, ViewerView.class, AdminView.class)
                .addAsLibraries(pom.resolve("org.apache.shiro:shiro-core:1.3.2")
                        .withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.apache.shiro:shiro-web:1.3.2")
                        .withTransitivity().asFile())
                .addAsWebInfResource(new ClassLoaderAsset("shiro.ini"),
                        ArchivePaths.create("shiro.ini"))
                .addAsWebInfResource(new ClassLoaderAsset("shiro.beans.xml"),
                        ArchivePaths.create("beans.xml"));
    }

    @Test
    @OperateOnDeployment("shiro")
    public void guestCannotAccessOtherViews() {
        // effectively logout, invalidates the session so need to reload
        login("", "");
        openWindow(NavigatableUI.class);
        waitForValue(By.id(AbstractShiroTestView.LABEL_ID), "Guest view");

        checkThatViewDisallowed(ViewerView.VIEW_ID);
        checkThatViewDisallowed(AdminView.VIEW_ID);
    }

    @Test
    @OperateOnDeployment("shiro")
    public void demoCanAccessViewerView() {
        login("demo", "demo");
        // check current user label in login pane
        waitForValue(By.id(LoginPane.CURRENT_USER_ID), "demo");

        checkThatViewAllowed(ViewerView.VIEW_ID);
        checkThatViewDisallowed(AdminView.VIEW_ID);
    }

    @Test
    @OperateOnDeployment("shiro")
    public void adminCanAccessAllViews() {
        login("admin", "admin");
        // check current user label in login pane
        waitForValue(By.id(LoginPane.CURRENT_USER_ID), "admin");

        checkThatViewAllowed(ViewerView.VIEW_ID);
        checkThatViewAllowed(AdminView.VIEW_ID);
    }

    private void checkThatViewAllowed(String viewString) {
        findElement(viewString).click();
        waitForValue(By.id(AbstractShiroTestView.LABEL_ID), viewString);
    }

    private void checkThatViewDisallowed(String viewString) {
        clickAndWait(viewString);
        String viewAfterClick = findElement(AbstractShiroTestView.LABEL_ID)
                .getText();
        assertThat(viewString, not(viewAfterClick));
    }

    private void login(String user, String password) {
        openWindow(NavigatableUI.class);
        waitForValue(By.id(AbstractShiroTestView.LABEL_ID), "Guest view");
        findElement(LoginPane.USER_ID).sendKeys(user);
        findElement(LoginPane.PASSWORD_ID).sendKeys(password);
        clickAndWait(LoginPane.LOGIN_ID);
    }

}
