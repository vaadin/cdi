package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.SessionUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SessionContextTest extends AbstractManagedCDIIntegrationTest {
    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("sessionScope",
                SessionUI.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        String uri = Conventions.deriveMappingForUI(SessionUI.class);
        openWindow(uri);
    }

    @Test
    public void testUIsAccessSameSession() throws Exception {
        Assert.assertEquals("", findElement(SessionUI.VALUELABEL_ID).getText());
        clickAndWait(SessionUI.SETVALUEBTN_ID);
        refreshWindow();//creates new UI
        Assert.assertEquals(SessionUI.VALUE, findElement(SessionUI.VALUELABEL_ID).getText());
    }

    @Test
    public void testSessionCloseDestroysSessionContext() throws Exception {
        Assert.assertEquals(0, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
        clickAndWait(SessionUI.INVALIDATEBTN_ID);
        Assert.assertEquals(1, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
    }
}
