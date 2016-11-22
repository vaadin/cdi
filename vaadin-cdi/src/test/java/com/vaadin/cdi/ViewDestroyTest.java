package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyViewUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ViewDestroyTest extends AbstractManagedCDIIntegrationTest {

    private String uiId;
    private String viewUri;

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewDestroy", DestroyViewUI.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
    }

    @Test
    public void testViewDestroyOnUIDestroy() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);

        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        assertViewDestroyCounts(0);

        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //open new UI. Navigating to home view on load triggers cleanup.
        openWindow(viewUri);
        assertViewDestroyCounts(1);
    }

    @Test
    public void testViewChangeDestroysViewScope() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);
        clickAndWait(DestroyViewUI.NAVIGATE_DEPENDENT_BTN_ID);
        assertViewDestroyCounts(1);
    }

    @Test
    public void testViewChangeDestroysDependentView() throws Exception {
        loadView(DestroyViewUI.DEPENDENT_VIEW);
        assertThat(getCount(DestroyViewUI.DependentView.DESTROY_COUNT + uiId), is(0));
        clickAndWait(DestroyViewUI.NAVIGATE_VIEW_BTN_ID);
        assertThat(getCount(DestroyViewUI.DependentView.DESTROY_COUNT + uiId), is(1));
    }

    private void assertViewDestroyCounts(int count) throws IOException {
        assertThat(getCount(DestroyViewUI.ViewScopedView.DESTROY_COUNT + uiId), is(count));
        assertThat(getCount(DestroyViewUI.ViewScopedBean.DESTROY_COUNT + uiId), is(count));
    }

    private void loadView(final String view) throws MalformedURLException {
        viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class) + "#!" + view;
        openWindow(viewUri);
        uiId = findElement(DestroyViewUI.UIID_ID).getText();
    }

}
