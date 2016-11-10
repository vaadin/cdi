package com.vaadin.cdi;

import com.vaadin.cdi.uis.ParameterizedNavigationUI;
import com.vaadin.cdi.views.RootView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RootViewAtContextRootTest extends AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment(name = "rootView")
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("rootView", RootView.class,
                ParameterizedNavigationUI.class);
    }

    @Test
    @OperateOnDeployment("rootView")
    public void testThatRootViewIsReachable() throws IOException {
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(0));
        assertThat(getCount(RootView.CONSTRUCT_COUNT), is(0));
        ParameterizedNavigationUI.NAVIGATE_TO = "";
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class));
        firstWindow.findElement(NAVIGATE_BUTTON).click();
        waitForValue(VIEW_LABEL, "default view");
        assertThat(getCount(ParameterizedNavigationUI.CONSTRUCT_COUNT), is(1));
        assertThat(getCount(RootView.CONSTRUCT_COUNT), is(1));
    }
}
