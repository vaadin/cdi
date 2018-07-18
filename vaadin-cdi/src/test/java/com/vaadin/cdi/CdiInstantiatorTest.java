/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Locale;

@RunWith(CdiTestRunner.class)
public class CdiInstantiatorTest {

    @Singleton
    public static class SomeCdiBean {
    }

    public static class ParentBean {

        @Inject
        private BeanManager bm;

        public BeanManager getBm() {
            return bm;
        }

    }

    @Vetoed
    public static class NotACdiBean extends ParentBean {
    }

    public static class Ambiguous extends ParentBean {
    }

    @VaadinServiceEnabled
    public static class I18NTestProvider implements I18NProvider {

        @Override
        public List<Locale> getProvidedLocales() {
            return null;
        }

        @Override
        public String getTranslation(String key, Locale locale,
                                     Object... params) {
            return null;
        }

    }

    @RequestScoped
    public static class ServiceInitObserver {

        ServiceInitEvent event;

        public void serviceInit(@Observes ServiceInitEvent event) {
            this.event = event;
        }

        public ServiceInitEvent getEvent() {
            return event;
        }

    }

    @Inject
    private BeanManager beanManager;

    @Inject
    @VaadinServiceEnabled
    private CdiInstantiator instantiator;

    @Inject
    private SomeCdiBean singleton;

    @Inject
    private ServiceInitObserver serviceInitObserver;

    private ServiceUnderTestContext serviceUnderTestContext;

    @Before
    public void setUp() {
        serviceUnderTestContext = new ServiceUnderTestContext(beanManager);
        serviceUnderTestContext.activate();
        CdiVaadinServletService service = serviceUnderTestContext.getService();
        Assert.assertTrue(instantiator.init(service));
    }

    @After
    public void tearDown() {
        serviceUnderTestContext.tearDownAll();
    }

    @Test
    public void getI18NProvider_beanEnabled_instanceReturned() {
        I18NProvider i18NProvider = instantiator.getI18NProvider();
        Assert.assertNotNull(i18NProvider);
        Assert.assertTrue((i18NProvider instanceof I18NTestProvider));
    }

    @Test
    public void getServiceInitListeners_javaSPIListenerExists_containsJavaSPIListener() {
        Assert.assertTrue(instantiator.getServiceInitListeners().anyMatch(
                listener -> listener instanceof JavaSPIVaadinServiceInitListener));
    }

    @Test
    public void getServiceInitListeners_eventFired_cdiObserverCalled() {
        VaadinService service = Mockito.mock(VaadinService.class);
        ServiceInitEvent event = new ServiceInitEvent(service);
        instantiator.getServiceInitListeners()
                .filter(listener -> listener.getClass().getPackage()
                        .equals(CdiInstantiator.class.getPackage()))
                .forEach(listener -> listener.serviceInit(event));
        Assert.assertSame(event, serviceInitObserver.getEvent());
    }

    @Test
    public void getOrCreate_beanSingleton_sameInstanceReturned() {
        Assert.assertSame(singleton,
                instantiator.getOrCreate(SomeCdiBean.class));
    }

    @Test
    public void getOrCreate_beanUnsatisfied_instantiatedAndInjectionOccurred() {
        NotACdiBean instance = instantiator.getOrCreate(NotACdiBean.class);
        Assert.assertNotNull(instance);
        Assert.assertNotNull(instance.getBm());
    }

    @Test
    public void getOrCreate_beanAmbiguous_instantiatedAndInjectionOccurred() {
        ParentBean instance = instantiator.getOrCreate(ParentBean.class);
        Assert.assertNotNull(instance);
        Assert.assertNotNull(instance.getBm());
    }

}
