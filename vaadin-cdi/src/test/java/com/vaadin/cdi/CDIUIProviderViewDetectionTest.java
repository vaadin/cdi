package com.vaadin.cdi;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.cdi.uis.PushStateSubUI;
import com.vaadin.cdi.uis.PushStateUI;
import com.vaadin.cdi.uis.RootUI;
import com.vaadin.cdi.uis.SubUI;
import com.vaadin.ui.UI;

@RunWith(MockitoJUnitRunner.class)
public class CDIUIProviderViewDetectionTest {

    @Mock
    BeanManager beanManager;

    @InjectMocks
    CDIUIProvider cut;

    @Before
    public void setUp() throws Exception {
        final Set<Bean<?>> beans = new LinkedHashSet<>(Arrays.asList(
                mockBean(RootUI.class), mockBean(SubUI.class),
                mockBean(PushStateUI.class), mockBean(PushStateSubUI.class)));

        when(beanManager.getBeans(eq(UI.class), isA(Annotation.class)))
                .thenReturn(beans);
    }

    private Bean<?> mockBean(Class<?> beanClass) {
        final Bean bean = mock(Bean.class);
        when(bean.getBeanClass()).thenReturn(beanClass);
        return bean;
    }

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
                cut.getUIBeanWithMapping("pushState/view/with/multiple/params")
                        .getBeanClass().getCanonicalName());
    }

    @Test
    public void testPushSateSubUI() {
        Assert.assertEquals(PushStateSubUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/sub").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateSubUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/sub/").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateSubUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/sub/view").getBeanClass()
                        .getCanonicalName());
        Assert.assertEquals(PushStateSubUI.class.getCanonicalName(),
                cut.getUIBeanWithMapping("pushState/sub/view/param")
                        .getBeanClass().getCanonicalName());
        Assert.assertEquals(PushStateSubUI.class.getCanonicalName(), cut
                .getUIBeanWithMapping("pushState/sub/view/with/multiple/params")
                .getBeanClass().getCanonicalName());
    }
}
