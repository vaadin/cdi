package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.ViewNavigationUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.context.ContextNotActiveException;

import static org.junit.Assert.assertEquals;

public class ViewNavigationTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewNavigation", ViewNavigationUI.class);
    }

    @Before
    public void setUp() throws Exception {
        String uri = Conventions.deriveMappingForUI(ViewNavigationUI.class);
        openWindow(uri);
    }

    @Test
    public void testRevertedNavigationRevertsViewScope() throws Exception {
        clickAndWait(ViewNavigationUI.REVERTED_NAV_BTN_ID);
        assertBeanValue(ViewNavigationUI.DEFAULTVIEW_VALUE);
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        assertBeanValue(ViewNavigationUI.SUCCESSVIEW_VALUE);
    }

    @Test
    public void testNavigationToSameViewCreatesNewContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        assertBeanValue(ViewNavigationUI.SUCCESSVIEW_VALUE);
        clickAndWait(ViewNavigationUI.CHANGE_VALUE_BTN_ID);
        assertBeanValue(ViewNavigationUI.CHANGEDSUCCESS_VALUE);

        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        assertBeanValue(ViewNavigationUI.SUCCESSVIEW_VALUE);
    }

    @Test
    public void testBeforeViewChangeFiredInOldContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        String value = findElement(ViewNavigationUI.BEFORE_VALUE_LABEL_ID).getText();
        assertEquals(ViewNavigationUI.DEFAULTVIEW_VALUE, value);
    }

    @Test
    public void testBeforeViewChangeWithoutOldViewThrowsContextNotActive() throws Exception {
        // happens on opening root view, so need no navigation
        String value = findElement(ViewNavigationUI.BEFORE_VALUE_LABEL_ID).getText();
        assertEquals(ContextNotActiveException.class.getSimpleName(), value);
    }

    @Test
    public void testAfterViewChangeFiredInNewContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        String value = findElement(ViewNavigationUI.AFTER_VALUE_LABEL_ID).getText();
        assertEquals(ViewNavigationUI.SUCCESSVIEW_VALUE, value);
    }

    private void assertBeanValue(String expectedValue) {
        String value = findElement(ViewNavigationUI.VALUE_LABEL_ID).getText();
        assertEquals(expectedValue, value);
    }

}
