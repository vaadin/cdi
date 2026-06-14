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
import com.vaadin.cdi.uis.ViewStrategyUI;
import org.junit.Before;

import java.io.IOException;

import static com.vaadin.cdi.uis.ViewStrategyUI.NAVBTN_ID;
import static com.vaadin.cdi.uis.ViewStrategyUI.TARGETSTATE_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

abstract class AbstractViewStrategyTest extends AbstractManagedCDIIntegrationTest {

    @Before
    public void setUp() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(ViewStrategyUI.class);
        openWindow(viewUri);
        resetCounts();
    }


    protected void assertDestroyCounts(int count) throws IOException {
        assertViewDestroyCount(count);
        assertBeanDestroyCount(count);
    }

    protected void assertConstructCounts(int count) throws IOException {
        assertViewConstructCount(count);
        assertBeanConstructCount(count);
    }


    protected void assertViewDestroyCount(int count) throws IOException {
        assertThat(getCount(getTestedViewDestroyCounter()), is(count));
    }

    protected void assertBeanDestroyCount(int count) throws IOException {
        assertThat(getCount(ViewStrategyUI.ViewScopedBean.DESTROY_COUNT), is(count));
    }

    protected abstract String getTestedViewDestroyCounter();

    protected abstract String getTestedViewConstructCounter();

    protected void assertBeanConstructCount(int count) throws IOException {
        assertThat(getCount(ViewStrategyUI.ViewScopedBean.CONSTRUCT_COUNT), is(count));
    }

    protected void assertViewConstructCount(int count) throws IOException {
        assertThat(getCount(getTestedViewConstructCounter()), is(count));
    }

    protected void assertBeanValue(String expectedValue) {
        String value = findElement(ViewStrategyUI.VALUE_LABEL_ID).getText();
        assertEquals(expectedValue, value);
    }

    protected void navigateTo(String viewState) {
        findElement(TARGETSTATE_ID).clear();
        findElement(TARGETSTATE_ID).sendKeys(viewState);
        clickAndWait(NAVBTN_ID);
    }

    protected void assertNop(String sourceState, String targetState, String beanValue)
            throws Exception {
        navigateTo(sourceState);
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertBeanValue(beanValue);

        navigateTo(targetState);
        // context hold
        assertConstructCounts(1);
        assertDestroyCounts(0);
        // no navigation happened - init not called again
        assertBeanValue(beanValue);
    }

    protected void assertAfterNavigateToOtherViewContextCreated(String sourceState, String srcBeanValue)
            throws Exception {
        navigateTo(sourceState);
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertThat(getCount(ViewStrategyUI.OtherView.CONSTRUCT_COUNT), is(0));
        assertBeanValue(srcBeanValue);

        navigateTo(ViewStrategyUI.OTHER);
        assertViewConstructCount(1);
        assertBeanConstructCount(2); // bean created again in new context
        assertDestroyCounts(1);
        assertThat(getCount(ViewStrategyUI.OtherView.CONSTRUCT_COUNT), is(1));
        assertBeanValue(",other:");
    }

    protected void assertAfterNavigateToTestedViewContextCreated(
            String sourceState, String targetState, String srcBeanValue, String targetBeanValue)
            throws Exception {
        navigateTo(sourceState);
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertBeanValue(srcBeanValue);

        navigateTo(targetState);
        assertViewConstructCount(2);
        assertBeanConstructCount(2);
        assertDestroyCounts(1);
        assertBeanValue(targetBeanValue);
    }
}
