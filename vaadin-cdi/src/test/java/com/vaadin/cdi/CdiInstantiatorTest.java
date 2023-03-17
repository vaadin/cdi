/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

import com.vaadin.flow.internal.UsageStatistics;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;

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

    @Singleton
    public static class SingletonComponent extends Div {
        @Inject
        BeanManager beanManager;
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

    @Test
    public void createComponent_componentIsCreated() {
        SingletonComponent component = instantiator
                .createComponent(SingletonComponent.class);
        Assert.assertNotNull(component);
        Assert.assertNotNull(component.beanManager);
    }

    @Test
    public void createComponent_componentIsCreatedOnEveryCall()
            throws ServletException {
        SingletonComponent component = instantiator
                .createComponent(SingletonComponent.class);
        Assert.assertNotNull(component);

        SingletonComponent anotherComponent = instantiator
                .createComponent(SingletonComponent.class);
        Assert.assertNotEquals(component, anotherComponent);
    }

    @Test
    public void init_callsUsageStatistics() {
        // @Before does instantiator.init()
        // There will be other entries too to filter out
        List<UsageStatistics.UsageEntry> entries = UsageStatistics.getEntries().filter(entry -> entry.getName().contains("Cdi")).collect(Collectors.toList());
        Assert.assertEquals(1, entries.size());

        UsageStatistics.UsageEntry entry = entries.get(0);
        Assert.assertEquals("flow/CdiInstantiator", entry.getName());
    }

}
