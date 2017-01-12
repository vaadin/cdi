package com.vaadin.cdi;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.cdi.internal.CrossInjectingBean;
import com.vaadin.cdi.uis.ParameterizedNavigationUI;
import com.vaadin.cdi.views.CrossInjectingView;

import java.net.MalformedURLException;

public class CrossInjectionTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "crossInjection")
    public static WebArchive crossInjectionArchive() {
        return ArchiveProvider.createWebArchive("crossInjection",
                ParameterizedNavigationUI.class, CrossInjectingView.class,
                CrossInjectingBean.class);
    }

    @Test
    @OperateOnDeployment("crossInjection")
    public void crossInjectionWithSetter() throws MalformedURLException {
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class) +
                ParameterizedNavigationUI.getNavigateToParam(""));
        findElement("navigate").click();
        waitForValue(By.id("view"), "CrossInjectingView");
        String id1 = findElement(CrossInjectingView.TRUE_ID).getText();
        String id2 = findElement(CrossInjectingView.SETTER_INJECTED_ID).getText();
        assertThat(id1, is(id2));
        assertThat(id1, not("null"));
    }

    @Test
    @OperateOnDeployment("crossInjection")
    public void crossInjectionWithConstructor() throws MalformedURLException {
        openWindow(deriveMappingForUI(ParameterizedNavigationUI.class) +
                ParameterizedNavigationUI.getNavigateToParam(""));
        findElement("navigate").click();
        waitForValue(By.id("view"), "CrossInjectingView");
        String id1 = findElement(CrossInjectingView.TRUE_ID).getText();
        String id2 = findElement(CrossInjectingView.CONSTRUCTOR_INJECTED_ID).getText();
        assertThat(id1, is(id2));
        assertThat(id1, not("null"));
    }
   
}
