package com.vaadin.cdi;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.cdi.internal.ProducedBean;
import com.vaadin.cdi.internal.UIScopedBean;
import com.vaadin.cdi.internal.ViewScopedBean;
import com.vaadin.cdi.uis.ConcurrentUI;
import com.vaadin.cdi.uis.NavigatableUI;
import com.vaadin.cdi.uis.ProducerUI;
import com.vaadin.cdi.views.AbstractNavigatableView;
import com.vaadin.cdi.views.UIScopedView;
import com.vaadin.cdi.views.ViewScopedView;

public class ScopedProducer extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "scopedProducer")
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
