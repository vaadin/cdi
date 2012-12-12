/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
