package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.myfaces.shared.util.Assert;
import org.hamcrest.core.Is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.vaadin.cdi.internal.CDIUtil;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.NonPassivatingBean;
import com.vaadin.cdi.internal.ViewScopedContext;
import com.vaadin.cdi.uis.NonPassivatingUI;
import com.vaadin.cdi.views.NonPassivatingContentView;

public class NonPassivatingBeanTest extends AbstractManagedCDIIntegrationTest {

    
    
    @Deployment(name = "nonPassivatingBean")
    public static WebArchive nonPassivatingBeanArchive() {
        return ArchiveProvider.createWebArchive("nonPassivatingBean",
                NonPassivatingBean.class, NonPassivatingUI.class, NonPassivatingContentView.class);
    }

    @Test
    @OperateOnDeployment("nonPassivatingBean")
    public void nonPassivatingBeanDoesntBreakVaadinCDI() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(NonPassivatingUI.class));
        String bean = findElement(NonPassivatingContentView.label_id).getText();
        assertThat(bean, startsWith("NonPassivatingBean"));
    }
    
    @Test
    public void testCustomNonPassivatingBeanInContext() throws MalformedURLException {
        openWindow(Conventions.deriveMappingForUI(NonPassivatingUI.class));
        String status = findElement(NonPassivatingContentView.custom_bean_id).getText();
        assertThat(status, equalTo(NonPassivatingContentView.success));
    }
    
   
}
