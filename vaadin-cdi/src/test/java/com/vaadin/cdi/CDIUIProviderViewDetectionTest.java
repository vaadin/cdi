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

    private void assertMapping(Class<? extends UI> uiClass, String mapping) {
        Assert.assertEquals(uiClass.getCanonicalName(),
                cut.getUIBeanWithMapping(mapping).getBeanClass().getCanonicalName());
    }

    @Test
    public void testRootUI() {
        assertMapping(RootUI.class, "");
        assertMapping(RootUI.class, cut.parseUIMapping("/!"));
        assertMapping(RootUI.class, cut.parseUIMapping("/!subUI"));
    }

    @Test
    public void testSubUI() {
        assertMapping(SubUI.class, "subUI");
    }

    @Test
    public void testPushSateUI() {
        assertMapping(PushStateUI.class, "pushState");
        assertMapping(PushStateUI.class, "pushState/");
        assertMapping(PushStateUI.class, "pushState/view");
        assertMapping(PushStateUI.class, "pushState/view/param");
        assertMapping(PushStateUI.class, "pushState/view/with/multiple/params");
    }

    @Test
    public void testPushSateSlashSubUI() {
        assertMapping(PushStateSubUI.class, "pushState/sub");
        assertMapping(PushStateSubUI.class, "pushState/sub/");
        assertMapping(PushStateSubUI.class, "pushState/sub/view");
        assertMapping(PushStateSubUI.class, "pushState/sub/view/param");
        assertMapping(PushStateSubUI.class, "pushState/sub/view/with/multiple/params");
    }

    @Test
    public void testPushSateBangSubUI() {
        assertMapping(PushStateUI.class, cut.parseUIMapping("pushState!sub"));
        assertMapping(PushStateUI.class, cut.parseUIMapping("pushState!sub/"));
        assertMapping(PushStateUI.class, cut.parseUIMapping("pushState!sub/view"));
        assertMapping(PushStateUI.class, cut.parseUIMapping("pushState!sub/view/param"));
        assertMapping(PushStateUI.class, cut.parseUIMapping("pushState!sub/view/with/multiple/params"));
    }
}
