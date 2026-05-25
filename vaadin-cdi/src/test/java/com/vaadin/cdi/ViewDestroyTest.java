/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
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
    public void testViewDestroyOnUIDestroy() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);

        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        assertViewDestroyCounts(1);
    }

    @Test
    public void testViewChangeDestroysViewScope() throws Exception {
        loadView(DestroyViewUI.VIEWSCOPED_VIEW);
        assertViewDestroyCounts(0);
        clickAndWait(DestroyViewUI.NAVIGATE_OTHER_BTN_ID);
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
        // Navigate to about:blank first to ensure a full page reload
        // (avoids issues when the same URL is loaded but the previous UI was closed)
        firstWindow.navigate().to("about:blank");
        String viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class) + "#!" + view;
        openWindow(viewUri);
        uiId = findElement(DestroyViewUI.UIID_ID).getText();
    }

}
