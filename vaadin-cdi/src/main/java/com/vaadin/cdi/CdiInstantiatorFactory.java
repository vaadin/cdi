package com.vaadin.cdi;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.server.VaadinService;

/**
 * Default CDI instantiator factory.
 * <p>
 * Can be overridden by a @{@link VaadinServiceEnabled} CDI
 * Alternative/Specializes, or can be customized with a Decorator.
 *
 * @see Instantiator
 */
@VaadinServiceScoped
@VaadinServiceEnabled
public class CdiInstantiatorFactory implements InstantiatorFactory {

    @Inject
    private BeanManager beanManager;

    @Override
    public Instantiator createInstantitor(VaadinService service) {
        if (!getServiceClass().isAssignableFrom(service.getClass())) {
            return null;
        }

        return new CdiInstantiator(beanManager, service);
    }

    public Class<? extends VaadinService> getServiceClass() {
        return CdiVaadinServletService.class;
    }

}
