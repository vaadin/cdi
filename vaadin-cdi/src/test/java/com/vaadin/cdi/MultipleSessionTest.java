package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.cdi.uis.MultipleSessionUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class MultipleSessionTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "multipleSessions", testable = false)
    public static WebArchive injectedBeanDependsOnSession() {
        return ArchiveProvider.createWebArchive("multipleSessions",
                MultipleSessionUI.class, MyBean.class);
    }

    @Test
    @OperateOnDeployment("multipleSessions")
    public void injectedBeanDependsOnSessionTest() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(MultipleSessionUI.class));

        (new WebDriverWait(firstWindow, 15)).until(ExpectedConditions
                .presenceOfElementLocated(By
                        .id(MultipleSessionUI.MAINSESSION2_ID)));

        String main = firstWindow.findElement(
                By.id(MultipleSessionUI.MAINSESSION_ID)).getText();
        String other = firstWindow.findElement(
                By.id(MultipleSessionUI.OTHERSESSION_ID)).getText();
        String main2 = firstWindow.findElement(
                By.id(MultipleSessionUI.MAINSESSION2_ID)).getText();

        assertThat(main, is(main2));
        assertThat(main, not(other));
    }

}
