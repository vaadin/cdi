/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
                mockBean(RootUI.class), mockBean(SubUI.class)));

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

}
