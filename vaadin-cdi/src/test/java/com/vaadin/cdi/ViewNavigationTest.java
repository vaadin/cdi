package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.ViewNavigationUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.context.ContextNotActiveException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

    @Test
    public void testAfterViewChangeCDIFiredInNewContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        String value = findElement(ViewNavigationUI.CDIAFTER_VALUE_LABEL_ID).getText();
        assertEquals(ViewNavigationUI.SUCCESSVIEW_VALUE, value);
    }

    private void assertBeanValue(String expectedValue) {
        String value = findElement(ViewNavigationUI.VALUE_LABEL_ID).getText();
        assertEquals(expectedValue, value);
    }

    @Test
    public void testBeforeLeaveFiredInOldContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        String value = findElement(ViewNavigationUI.BEFORE_LEAVE_VALUE_LABEL_ID).getText();
        assertEquals(ViewNavigationUI.DEFAULTVIEW_VALUE, value);
    }

    @Test
    public void testShowViewCalledInNewContext() throws Exception {
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        String value = findElement(ViewNavigationUI.SHOW_VIEW_VALUE_LABEL_ID).getText();
        assertEquals(ViewNavigationUI.SUCCESSVIEW_VALUE, value);
    }

    @Test
    public void testDelayedNavigationFindsInactiveOpeningContext() throws Exception {
        resetCounts();
        clickAndWait(ViewNavigationUI.DELAY_NAV_BTN_ID);
        assertBeanValue(ViewNavigationUI.DELAYVIEW_VALUE);
        assertThat(getCount(ViewNavigationUI.SuccessView.CONSTRUCT_COUNT), is(0));

        // navigation is delayed in DelayNavigationView.beforeLeave()
        clickAndWait(ViewNavigationUI.SUCCESS_NAV_BTN_ID);
        // target view is constructed
        assertThat(getCount(ViewNavigationUI.SuccessView.CONSTRUCT_COUNT), is(1));
        // actual context remains active
        assertBeanValue(ViewNavigationUI.DELAYVIEW_VALUE);

        // perform delayed navigation. The target view now belongs to the inactive opening context.
        clickAndWait(ViewNavigationUI.DelayNavigationView.PREFORM_DELAYED_NAV_BTN_ID);
        assertBeanValue(ViewNavigationUI.SUCCESSVIEW_VALUE);
        assertThat(getCount(ViewNavigationUI.SuccessView.CONSTRUCT_COUNT), is(1));
    }

}
