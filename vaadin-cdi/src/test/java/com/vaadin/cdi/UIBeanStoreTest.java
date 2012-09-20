package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.cdi.uis.InstrumentedUI;

public class UIBeanStoreTest {

    private UIBeanStore cut;

    @Before
    public void before() {
        this.cut = new UIBeanStore();
    }

    @Test
    public void getUIFromEmptyStore() {
        Bean bean = mock(Bean.class);
        when(bean.getBeanClass()).thenReturn(InstrumentedUI.class);
        Object instance = cut.getBeanInstance(bean);
        assertNull(instance);
    }

    @Test
    public void getUIFromEmptyStoreWithNullCreationalContext() {
        Bean bean = mock(Bean.class);
        when(bean.getBeanClass()).thenReturn(InstrumentedUI.class);
        Object instance = cut.getBeanInstance(bean, null);
        assertNull(instance);
    }

    @Test
    public void getUIFromEmptyStoreWithCreationalCreationalContext() {
        Bean bean = mock(Bean.class);
        CreationalContext creationalContext = mock(CreationalContext.class);
        InstrumentedUI expected = new InstrumentedUI();
        when(bean.create(creationalContext)).thenReturn(expected);

        when(bean.getBeanClass()).thenReturn(InstrumentedUI.class);
        InstrumentedUI instance = (InstrumentedUI) cut.getBeanInstance(bean,
                creationalContext);
        assertNotNull(instance);
        assertThat(instance, is(expected));
    }
}
