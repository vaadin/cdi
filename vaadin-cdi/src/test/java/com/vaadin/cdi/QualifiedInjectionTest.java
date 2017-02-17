package com.vaadin.cdi;

import com.vaadin.cdi.internal.*;
import com.vaadin.cdi.uis.QualifierInjectionUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import java.net.MalformedURLException;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

public class QualifiedInjectionTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "qualifiedInjection", testable = false)
    public static WebArchive qualifiedInjectionArchive() {
        return ArchiveProvider.createWebArchive("qualifiedInjection",
                QualifierInjectionUI.class, MyBean.class, AlphaBean.class,
                BetaBean.class, Alpha.class, Beta.class);
    }

    @Test
    @OperateOnDeployment("qualifiedInjection")
    public void injectionObeysQualifiers() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(QualifierInjectionUI.class));

        String defaultBean = firstWindow.findElement(
                By.id(QualifierInjectionUI.DEFAULT_ID)).getText();
        String alphaBean = firstWindow.findElement(
                By.id(QualifierInjectionUI.ALPHA_ID)).getText();
        String betaBean = firstWindow.findElement(
                By.id(QualifierInjectionUI.BETA_ID)).getText();

        assertThat(defaultBean, startsWith("MyBean"));
        assertThat(alphaBean, startsWith("AlphaBean"));
        assertThat(betaBean, startsWith("BetaBean"));
    }
}
