package com.vaadin.cdi;

import com.vaadin.cdi.uis.ConcurrentUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MultipleAccessIsolationTest extends
        AbstractManagedCDIIntegrationTest {

    @Before
    public void resetCounter() throws IOException {
        resetCounts();
    }

    @Deployment(name = "concurrentAccess", testable = false)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("concurrentAccess",
                ConcurrentUI.class);
    }

    @Test
    public void testConcurrentAccess() throws IOException,
            InterruptedException {
        String uri = deriveMappingForUI(ConcurrentUI.class);
        assertThat(getCount(ConcurrentUI.CONSTRUCT_COUNT), is(0));
        openWindow(firstWindow, uri);
        assertThat(getCount(ConcurrentUI.CONSTRUCT_COUNT), is(1));
        firstWindow.findElement(By.id(ConcurrentUI.OPEN_WINDOW)).click();
        waitForNumberOfWindowsToEqual(2);
        Thread.sleep(500);
        List<String> handles = new ArrayList<String>(
                firstWindow.getWindowHandles());
        firstWindow.switchTo().window(handles.get(0));
        firstWindow.findElement(By.id(ConcurrentUI.COUNTER_BUTTON)).click();
        firstWindow.findElement(By.id(ConcurrentUI.COUNTER_BUTTON)).click();
        firstWindow.findElement(By.id(ConcurrentUI.COUNTER_BUTTON)).click();
        waitForValue(By.id(ConcurrentUI.COUNTER_LABEL), 3);
        firstWindow.switchTo().window(handles.get(1));
        (new WebDriverWait(firstWindow, 15)).until(ExpectedConditions
                .presenceOfElementLocated(LABEL));
        // the second window was loaded before the clicks on the first one so
        // the counter is always 0
        assertThat(firstWindow.findElement(By.id(ConcurrentUI.COUNTER_LABEL))
                .getText(), is("0"));
        // this updates the counter, taking into account what might have
        // happened on the server
        firstWindow.findElement(By.id(ConcurrentUI.COUNTER_BUTTON)).click();
        waitForValue(By.id(ConcurrentUI.COUNTER_LABEL), 1);
    }

    @Test
    public void testConsecutiveAccess() throws IOException,
            InterruptedException {
        String uri = deriveMappingForUI(ConcurrentUI.class);
        assertThat(getCount(ConcurrentUI.CONSTRUCT_COUNT), is(0));
        openWindow(firstWindow, uri);
        firstWindow.findElement(By.id(ConcurrentUI.COUNTER_BUTTON)).click();
        Thread.sleep(100);
        assertThat(firstWindow.findElement(By.id(ConcurrentUI.COUNTER_LABEL))
                .getText(), is("1"));
        firstWindow.navigate().refresh();
        (new WebDriverWait(firstWindow, 15)).until(ExpectedConditions
                .presenceOfElementLocated(LABEL));
        assertThat(firstWindow.findElement(By.id(ConcurrentUI.COUNTER_LABEL))
                .getText(), is("0"));
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
