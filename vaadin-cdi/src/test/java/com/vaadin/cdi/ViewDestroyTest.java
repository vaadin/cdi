package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyViewUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ViewDestroyTest extends AbstractManagedCDIIntegrationTest {

    private String viewUri;

    @Deployment(name = "viewDestroy", testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewDestroy", DestroyViewUI.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class)+ "#!home";
        openWindow(viewUri);
        assertViewDestroyCounts(0);
    }

    @Test
    @OperateOnDeployment("viewDestroy")
    public void testViewDestroyOnUIDestroy() throws Exception {
        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        openWindow(viewUri);
        assertViewDestroyCounts(0);
        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //open new UI. Navigating to home view on load triggers cleanup.
        openWindow(viewUri);

        assertViewDestroyCounts(2);
    }

    @Test
    @OperateOnDeployment("viewDestroy")
    public void testViewChangeDestroysViewScope() throws Exception {
        //ViewChange event triggers a cleanup
        clickAndWait(DestroyViewUI.NAVIGATE_BTN_ID);

        assertViewDestroyCounts(1);
    }

    private void assertViewDestroyCounts(int count) throws IOException {
        assertThat(getCount(DestroyViewUI.VIEW_DESTROY_COUNT_KEY), is(count));
        assertThat(getCount(DestroyViewUI.VIEWBEAN_DESTROY_COUNT_KEY), is(count));
    }

}
