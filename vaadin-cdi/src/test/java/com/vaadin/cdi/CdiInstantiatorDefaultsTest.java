package com.vaadin.cdi;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.cdi.context.ServiceUnderTestContext;
import com.vaadin.cdi.context.VaadinServiceScopedContext;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.DefaultMenuAccessControl;
import com.vaadin.flow.server.auth.MenuAccessControl;

@EnableWeld
public class CdiInstantiatorDefaultsTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(
            CdiInstantiatorFactory.class, CdiInstantiator.class,
            VaadinServiceScopedContext.ContextualStorageManager.class
    ).activate(VaadinServiceScoped.class).build();

    @Inject
    private BeanManager beanManager;

    @Inject
    @VaadinServiceEnabled
    private CdiInstantiatorFactory instantiatorFactory;

    private Instantiator instantiator;

    private ServiceUnderTestContext serviceUnderTestContext;

    @BeforeEach
    public void setUp() {
        serviceUnderTestContext = new ServiceUnderTestContext(beanManager);
        serviceUnderTestContext.activate();
        instantiator = instantiatorFactory.createInstantitor(VaadinService.getCurrent());
    }

    @AfterEach
    public void tearDown() {
        serviceUnderTestContext.tearDownAll();
    }

    @Test
    public void getMenuAccessControl_beanNotProvided_instanceReturned() {
        MenuAccessControl menuAccessControl = instantiator
                .getMenuAccessControl();
        Assertions.assertNotNull(menuAccessControl);
        Assertions.assertInstanceOf(DefaultMenuAccessControl.class, menuAccessControl);
    }
}
