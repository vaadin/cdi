package com.vaadin.cdi;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.cdi.uis.PushStateUI;
import com.vaadin.cdi.uis.RootUI;
import com.vaadin.cdi.uis.SubUI;

public class CDIUIProviderViewDetectionTest
        extends AbstractManagedCDIIntegrationTest {

    @Deployment
    public static WebArchive createJavaTestArchive() {
        return ArchiveProvider.createWebArchive("uiProvider", RootUI.class,
                SubUI.class, PushStateUI.class);
    }

    @Inject
    CDIUIProvider cut;

    @Test
    public void testRootUI() {
        Assert.assertEquals(RootUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("").getBeanClass().getCanonicalName());
    }

    @Test
    public void testSubUI() {
        Assert.assertEquals(SubUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("subUI").getBeanClass()
                        .getCanonicalName());
    }

    @Test
    public void testPushSateUI() {
        Assert.assertEquals(PushStateUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/view").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/view/param").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/view/with/multiple/params").getBeanClass()
                        .getCanonicalName());
    }

}
