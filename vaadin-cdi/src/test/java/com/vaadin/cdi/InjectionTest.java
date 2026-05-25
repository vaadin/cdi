/*
 * Vaadin CDI Add-on
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
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.cdi.uis.InjectionUI;
import com.vaadin.cdi.views.BeanView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

//@Ignore("Arquillian integration test - requires an application server container profile and browser")
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

        (new WebDriverWait(firstWindow, Duration.ofSeconds(15))).until(ExpectedConditions
                .presenceOfElementLocated(By.id(InjectionUI.beanId1)));

        String bean11 = firstWindow.findElement(By.id(InjectionUI.beanId1))
                .getText();
        String bean21 = firstWindow.findElement(By.id(InjectionUI.beanId2))
                .getText();

        assertThat(bean11, is(bean21));

        refreshWindow();

        (new WebDriverWait(firstWindow, Duration.ofSeconds(15))).until(ExpectedConditions
                .presenceOfElementLocated(By.id(InjectionUI.beanId1)));

        String bean12 = firstWindow.findElement(By.id(InjectionUI.beanId1))
                .getText();
        String bean22 = firstWindow.findElement(By.id(InjectionUI.beanId2))
                .getText();

        assertThat(bean12, is(bean22));
        assertThat(bean11, not(bean12));

    }

}
