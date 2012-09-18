package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.cdi.uis.EmptyUI;

public class UIBeanStoreTest {

    private UIBeanStore cut;

    @Before
    public void before() {
        this.cut = new UIBeanStore();
    }

    @Test
    public void getUIFromEmptyStore() {
        Bean bean = mock(Bean.class);
        when(bean.getBeanClass()).thenReturn(EmptyUI.class);
        Object instance = cut.getBeanInstance(bean);
        assertNull(instance);
    }

    @Test
    public void getUIFromEmptyStoreWithNullCreationalContext() {
        Bean bean = mock(Bean.class);
        when(bean.getBeanClass()).thenReturn(EmptyUI.class);
        Object instance = cut.getBeanInstance(bean, null);
        assertNull(instance);
    }

    @Test
    public void getUIFromEmptyStoreWithCreationalCreationalContext() {
        Bean bean = mock(Bean.class);
        CreationalContext creationalContext = mock(CreationalContext.class);
        EmptyUI expected = new EmptyUI();
        when(bean.create(creationalContext)).thenReturn(expected);

        when(bean.getBeanClass()).thenReturn(EmptyUI.class);
        EmptyUI instance = (EmptyUI) cut.getBeanInstance(bean,
                creationalContext);
        assertNotNull(instance);
        assertThat(instance, is(expected));
    }
}
