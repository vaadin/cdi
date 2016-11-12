package com.vaadin.cdi;

import com.vaadin.cdi.internal.ProducedBean;
import com.vaadin.cdi.uis.ProducerUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ScopedProducerTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "scopedProducer", testable = false)
    public static WebArchive scopedProducerArchive() {
        return ArchiveProvider.createWebArchive("scopedProducer",
                ProducerUI.class, ProducedBean.class);
    }

    @Test
    @OperateOnDeployment("scopedProducer")
    public void uiScopedProducer() {
        openWindow(ProducerUI.class);

        String bean1 = findElement(ProducerUI.BEAN1_ID).getText();
        String bean2 = findElement(ProducerUI.BEAN2_ID).getText();
        String expected = "produced/";
        assertThat(bean1, startsWith(expected));
        assertThat(bean1, is(bean2));
    }

    @Test
    @OperateOnDeployment("scopedProducer")
    public void separateUIs() {
        openWindow(ProducerUI.class);
        findElement(ProducerUI.WINDOW_OPEN_ID).click();
        waitForNumberOfWindowsToEqual(2);
        List<String> handles = new ArrayList<String>(
                firstWindow.getWindowHandles());
        firstWindow.switchTo().window(handles.get(0));
        String bean1 = findElement(ProducerUI.BEAN1_ID).getText();
        firstWindow.switchTo().window(handles.get(1));
        String bean2 = findElement(ProducerUI.BEAN1_ID).getText();
        assertThat(bean1, not(bean2));
    }

    @SuppressWarnings("unchecked")
    private void waitForNumberOfWindowsToEqual(final int numberOfWindows) {
        (new WebDriverWait(firstWindow, 30)).until(new ExpectedCondition() {
            @Override
            public Object apply(Object input) {
                return firstWindow.getWindowHandles().size() == numberOfWindows;
            }
        });
    }
}
