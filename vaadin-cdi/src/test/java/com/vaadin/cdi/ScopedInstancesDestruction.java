package com.vaadin.cdi;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.cdi.internal.UIScopedBean;
import com.vaadin.cdi.internal.ViewScopedBean;
import com.vaadin.cdi.uis.NavigatableUI;
import com.vaadin.cdi.views.AbstractNavigatableView;
import com.vaadin.cdi.views.AbstractScopedInstancesView;
import com.vaadin.cdi.views.UIScopedView;
import com.vaadin.cdi.views.ViewScopedView;

public class ScopedInstancesDestruction extends AbstractManagedCDIIntegrationTest {

    private static final String DEPLOYMENT_NAME = "scopedDestruction";

    @Deployment(name = DEPLOYMENT_NAME)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("scopedDestruction",
                UIScopedView.class, AbstractScopedInstancesView.class,
                AbstractNavigatableView.class, ViewScopedView.class, NavigatableUI.class,
                UIScopedBean.class, ViewScopedBean.class);
    }

    @Test
    @OperateOnDeployment(DEPLOYMENT_NAME)
    public void viewScopeDestruction() throws MalformedURLException {
        openWindow(deriveMappingForUI(NavigatableUI.class));
        // ViewScoped view instance opens initially

        String firstDestroyCount = getTextById(ViewScopedView.DESTROY_COUNT_LABEL);

        // Open UIScoped view instance
        navigateToUIScoped();

        // Navigate back to the ViewScoped view
        navigateToViewScoped();

        // We should have received a new instance and @PreDestroy method should have been already
        // invoked on first instance

        String secondDestroyCount = getTextById(ViewScopedView.DESTROY_COUNT_LABEL);

        assertThat(secondDestroyCount, not(firstDestroyCount));
    }

    private String getTextById(String id) {
        return firstWindow.findElement(By.id(id)).getText();
    }

    private void navigateToUIScoped() {
        firstWindow.findElement(
                By.id(AbstractScopedInstancesView.NAVIGATE_TO_UISCOPED)).click();
        waitForValue(By.id(UIScopedView.DESCRIPTION_LABEL), "UIScopedView");
    }

    private void navigateToViewScoped() {
        firstWindow.findElement(
                By.id(AbstractScopedInstancesView.NAVIGATE_TO_VIEWSCOPED)).click();
        waitForValue(By.id(ViewScopedView.DESCRIPTION_LABEL), "ViewScopedView");
    }

}
