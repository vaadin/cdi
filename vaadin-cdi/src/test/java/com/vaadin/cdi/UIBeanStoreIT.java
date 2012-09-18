package com.vaadin.cdi;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.uis.EmptyUI;

@RunWith(Arquillian.class)
public class UIBeanStoreIT {

    @Inject
    UIBeanStore cut;

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive(EmptyUI.class);
    }

    @Test
    public void getUIFromEmptyStore() {
        Bean bean = mock(Bean.class);
        when(bean.getBeanClass()).thenReturn(EmptyUI.class);
        Object instance = cut.getBeanInstance(bean);
        assertNull(instance);
    }

}
