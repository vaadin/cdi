package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyUI;
import com.vaadin.cdi.uis.ScopedInstrumentedView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UIDestroyTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "uiDestroy")
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("uiDestroy",
                DestroyUI.class,
                ScopedInstrumentedView.class);
    }

    @Test
    @OperateOnDeployment("uiDestroy")
    public void testViewChangeTriggersCleanup() throws Exception {
        DestroyUI.resetCounter();
        assertThat(DestroyUI.getNumberOfInstances(), is(0));
        String uri = Conventions.deriveMappingForUI(DestroyUI.class);

        openWindow(uri);
        //close first UI, wait for response
        firstWindow.findElement(By.id(DestroyUI.CLOSE_BTN_ID)).click();
        waitForValue(LABEL, DestroyUI.CLOSE_BTN_ID);
        //open new UI
        openWindow(uri);

        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY
        //still have 2 UIs
        assertThat(DestroyUI.getNumberOfInstances(), is(2));

        //ViewChange event triggers a cleanup
        firstWindow.findElement(By.id(DestroyUI.NAVIGATE_BTN_ID)).click();
        waitForValue(LABEL, DestroyUI.NAVIGATE_BTN_ID);

        assertThat(DestroyUI.getNumberOfInstances(), is(1));
    }

}
