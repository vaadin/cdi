package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.cdi.uis.InjectionUI;
import com.vaadin.cdi.views.BeanView;
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

public class InjectionTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "uiInjection", testable = false)
    public static WebArchive alternativeAndActiveWithSamePath() {
        return ArchiveProvider.createWebArchive("uiInjection",
                InjectionUI.class, MyBean.class, BeanView.class);
    }

    @Test
    @OperateOnDeployment("uiInjection")
    public void testUIInjection() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(InjectionUI.class));

        (new WebDriverWait(firstWindow, 15)).until(ExpectedConditions
                .presenceOfElementLocated(By.id(InjectionUI.beanId1)));

        String bean11 = firstWindow.findElement(By.id(InjectionUI.beanId1))
                .getText();
        String bean21 = firstWindow.findElement(By.id(InjectionUI.beanId2))
                .getText();

        assertThat(bean11, is(bean21));

        refreshWindow();

        (new WebDriverWait(firstWindow, 15)).until(ExpectedConditions
                .presenceOfElementLocated(By.id(InjectionUI.beanId1)));

        String bean12 = firstWindow.findElement(By.id(InjectionUI.beanId1))
                .getText();
        String bean22 = firstWindow.findElement(By.id(InjectionUI.beanId2))
                .getText();

        assertThat(bean12, is(bean22));
        assertThat(bean11, not(bean12));

    }

}
