package com.vaadin.cdi;

import java.net.MalformedURLException;

import com.vaadin.cdi.uis.ParameterizedNavigationUI;
import com.vaadin.cdi.views.RootView;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import static com.vaadin.cdi.internal.ConventionsAccess.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RootViewAtContextRoot extends AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() {
        RootView.reset();
    }

    @Deployment(name = "rootView")
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("rootView", RootView.class,
                ParameterizedNavigationUI.class);
    }

    @Test
    @OperateOnDeployment("rootView")
    public void testThatRootViewIsReachable() throws MalformedURLException {
        assertThat(ParameterizedNavigationUI.getNumberOfInstances(), is(0));
        assertThat(RootView.getNumberOfInstances(), is(0));
        ParameterizedNavigationUI.NAVIGATE_TO = "";
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class));
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "default view");
        assertThat(ParameterizedNavigationUI.getNumberOfInstances(), is(1));
        assertThat(RootView.getNumberOfInstances(), is(1));
    }
}
