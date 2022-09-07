package com.vaadin.cdi.itest;

import com.vaadin.cdi.itest.beandiscoverymode.BeanDiscoveryModeView;
import com.vaadin.cdi.itest.beandiscoverymode.CdiComponentGreetService;
import com.vaadin.cdi.itest.beandiscoverymode.NormalScopedGreetService;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.vaadin.cdi.itest.beandiscoverymode.BeanDiscoveryModeView.CLEAR_NAME_BTN_ID;
import static com.vaadin.cdi.itest.beandiscoverymode.BeanDiscoveryModeView.CDI_SRV_BTN_ID;
import static com.vaadin.cdi.itest.beandiscoverymode.BeanDiscoveryModeView.NORMAL_SRV_BTN_ID;
import static com.vaadin.cdi.itest.beandiscoverymode.BeanDiscoveryModeView.RESULT_SPAN_ID;
import static com.vaadin.cdi.itest.beandiscoverymode.BeanDiscoveryModeView.SET_NAME_BTN_ID;

public class BeanDiscoveryModeTest extends AbstractCdiTest {

    private static final Class<?>[] CLASSES = {
            BeanDiscoveryModeView.class,
            CdiComponentGreetService.class,
            NormalScopedGreetService.class
    };

    @Deployment(name = "bean-discovery-mode-annotated", testable = false)
    public static WebArchive createImplicitBeanArchiveDeployment() {
        return ArchiveProvider
                .createWebArchive("bean-discovery-mode-annotated-test", CLASSES)
                .addAsWebInfResource(ArchiveProvider.class.getClassLoader()
                        .getResource("beans.xml"), "beans.xml");
    }

    @Deployment(name = "bean-discovery-mode-all", testable = false)
    public static WebArchive createCdiServletEnabledDeployment() {
        return ArchiveProvider
                .createWebArchive("bean-discovery-mode-all-test", CLASSES);
    }

    @Before
    public void setUp() {
        open();
    }

    @After
    public void cleanUp() {
        click(CLEAR_NAME_BTN_ID);
    }

    @Test
    @OperateOnDeployment("bean-discovery-mode-annotated")
    public void beanDiscoveryWorks_when_beanDiscoveryModeIsAnnotated() {
        validateBeanDiscoveryAndInjectionWorks();
    }

    @Test
    @OperateOnDeployment("bean-discovery-mode-all")
    public void beanDiscoveryWorks_when_beanDiscoveryModeIsAll() {
        validateBeanDiscoveryAndInjectionWorks();
    }

    private void validateBeanDiscoveryAndInjectionWorks() {
        click(SET_NAME_BTN_ID);
        click(NORMAL_SRV_BTN_ID);
        waitUntilNot(driver -> getText(RESULT_SPAN_ID) == null);
        Assert.assertEquals("Hello MyName", getText(RESULT_SPAN_ID));

        cleanUp();

        click(SET_NAME_BTN_ID);
        click(CDI_SRV_BTN_ID);
        waitUntilNot(driver -> getText(RESULT_SPAN_ID) == null);
        Assert.assertEquals("Hello MyName", getText(RESULT_SPAN_ID));
    }
}
