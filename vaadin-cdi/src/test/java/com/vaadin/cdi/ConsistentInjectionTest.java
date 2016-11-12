package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.cdi.uis.ConsistentInjectionUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import java.net.MalformedURLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConsistentInjectionTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "consistentInjection", testable = false)
    public static WebArchive initAndPostConstructAreConsistent() {
        return ArchiveProvider.createWebArchive("consistentInjection",
                ConsistentInjectionUI.class, MyBean.class);
    }

    @Test
    @OperateOnDeployment("consistentInjection")
    public void initAndPostConstructInjectionsAreConsistent()
            throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(ConsistentInjectionUI.class));

        String postConstruct = firstWindow.findElement(
                By.id(ConsistentInjectionUI.POSTCONSTRUCT_ID)).getText();
        String init = firstWindow.findElement(
                By.id(ConsistentInjectionUI.INIT_ID)).getText();

        assertThat(postConstruct, is(init));
    }

}
