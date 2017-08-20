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

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewDestroy", DestroyViewUI.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
    }

    @Test
    public void testViewChangeDestroysViewScope() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);
        clickAndWait(DestroyViewUI.NAVIGATE_OTHER_BTN_ID);
        assertViewDestroyCounts(1);
    }

    @Test
    public void testNaviagteToSameViewDestroysViewScope() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);
        clickAndWait(DestroyViewUI.NAVIGATE_VIEW_BTN_ID);
        assertViewDestroyCounts(1);
    }

    @Test
    public void testChangeToNonCdiViewDestroysViewScope() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);
        clickAndWait(DestroyViewUI.NAVIGATE_ERROR_BTN_ID);
        assertViewDestroyCounts(1);
    }

    private void assertViewDestroyCounts(int count) throws IOException {
        assertThat(getCount(DestroyViewUI.ViewScopedView.DESTROY_COUNT + uiId), is(count));
        assertThat(getCount(DestroyViewUI.ViewScopedBean.DESTROY_COUNT + uiId), is(count));
    }

    private void loadView(final String view) throws MalformedURLException {
        String viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class) + "#!" + view;
        openWindow(viewUri);
        uiId = findElement(DestroyViewUI.UIID_ID).getText();
    }

}
