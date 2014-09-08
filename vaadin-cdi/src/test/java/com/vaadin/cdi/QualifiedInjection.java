package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.cdi.internal.Alpha;
import com.vaadin.cdi.internal.AlphaBean;
import com.vaadin.cdi.internal.Beta;
import com.vaadin.cdi.internal.BetaBean;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.cdi.uis.QualifierInjectionUI;

public class QualifiedInjection extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "qualifiedInjection")
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
