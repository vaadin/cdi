package com.vaadin.cdi;

import java.net.MalformedURLException;

import com.vaadin.cdi.internal.UIScopedBean;
import com.vaadin.cdi.internal.ViewScopedBean;
import com.vaadin.cdi.uis.NavigatableUI;
import com.vaadin.cdi.views.AbstractNavigatableView;
import com.vaadin.cdi.views.AbstractScopedInstancesView;
import com.vaadin.cdi.views.UIScopedView;
import com.vaadin.cdi.views.ViewScopedView;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.vaadin.cdi.internal.ConventionsAccess.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ScopedInstances extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "scopedNavigation")
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("scopedNavigation",
                UIScopedView.class, AbstractScopedInstancesView.class,
                AbstractNavigatableView.class, ViewScopedView.class, NavigatableUI.class,
                UIScopedBean.class, ViewScopedBean.class);
    }

    @Test
    @OperateOnDeployment("scopedNavigation")
    public void viewInstancesInContexts() throws MalformedURLException {
        openWindow(deriveMappingForUI(NavigatableUI.class));
        // ViewScoped view instance opens initially

        String firstViewInstance = getTextById(ViewScopedView.INSTANCE_LABEL);

        // Open UIScoped view instance
        navigateToUIScoped();

        String firstUIInstance = getTextById(UIScopedView.INSTANCE_LABEL);

        // Navigate back to the ViewScoped view
        navigateToViewScoped();

        // We should have received a new instance instead of being served the
        // same one
        String secondViewInstance = getTextById(ViewScopedView.INSTANCE_LABEL);
        assertThat(firstViewInstance, not(secondViewInstance));

        // Navigate back to the UIScoped view
        navigateToUIScoped();

        // We should have received the same instance as before.
        String secondUIInstance = getTextById(UIScopedView.INSTANCE_LABEL);
        assertThat(firstUIInstance, is(secondUIInstance));

    }

    @Test
    @OperateOnDeployment("scopedNavigation")
    public void injectedInstancesInContexts() throws MalformedURLException {
        openWindow(deriveMappingForUI(NavigatableUI.class));
        // ViewScoped view instance opens initially

        String firstViewScopedInViewScoped = getTextById(ViewScopedBean.ID);
        String firstUIScopedInViewScoped = getTextById(UIScopedBean.ID);

        // Open UIScoped view instance
        navigateToUIScoped();

        String firstViewScopedInUIScoped = getTextById(ViewScopedBean.ID);
        String firstUIScopedInUIScoped = getTextById(UIScopedBean.ID);

        // Navigate back to the ViewScoped view
        navigateToViewScoped();

        // We should have received a new instance instead of being served the
        // same one
        String secondViewScopedInViewScoped = getTextById(ViewScopedBean.ID);
        String secondUIScopedInViewScoped = getTextById(UIScopedBean.ID);


        // Navigate back to the UIScoped view
        navigateToUIScoped();

        // We should have received the same instance as before.
        String secondViewScopedInUIScoped = getTextById(ViewScopedBean.ID);
        String secondUIScopedInUIScoped = getTextById(UIScopedBean.ID);

        // ViewScoped instances should be unique, UIScoped instances should be
        // shared.
        assertThat(firstViewScopedInViewScoped, not(firstViewScopedInUIScoped));
        assertThat(firstViewScopedInViewScoped,
                not(secondViewScopedInViewScoped));
        assertThat(firstViewScopedInViewScoped, not(secondViewScopedInUIScoped));
        assertThat(secondViewScopedInViewScoped, not(firstViewScopedInUIScoped));
        assertThat(secondViewScopedInViewScoped,
                not(secondViewScopedInUIScoped));
        assertThat(firstViewScopedInUIScoped, not(secondViewScopedInUIScoped));

        assertThat(firstUIScopedInViewScoped, is(firstUIScopedInUIScoped));
        assertThat(firstUIScopedInViewScoped, is(secondUIScopedInViewScoped));
        assertThat(firstUIScopedInViewScoped, is(secondUIScopedInUIScoped));
        assertThat(secondUIScopedInViewScoped, is(firstUIScopedInUIScoped));
        assertThat(secondUIScopedInViewScoped, is(secondUIScopedInUIScoped));
        assertThat(firstUIScopedInUIScoped, is(secondUIScopedInUIScoped));

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
