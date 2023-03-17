/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Unmanaged;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinService;

/**
 * Default CDI instantiator.
 * <p>
 * Can be overridden by a @{@link VaadinServiceEnabled} CDI
 * Alternative/Specializes, or can be customized with a Decorator.
 *
 * @see Instantiator
 */
@VaadinServiceScoped
@VaadinServiceEnabled
public class CdiInstantiator extends AbstractCdiInstantiator {

    @Inject
    private BeanManager beanManager;

    @Override
    public BeanManager getBeanManager() {
        return beanManager;
    }

    @Override
    public Class<? extends VaadinService> getServiceClass() {
        return CdiVaadinServletService.class;
    }

    @Override
    public <T extends Component> T createComponent(Class<T> componentClass) {
        Unmanaged<T> unmanagedClass = new Unmanaged<T>(componentClass);
        Unmanaged.UnmanagedInstance<T> instance = unmanagedClass.newInstance();
        instance.produce().inject().postConstruct();
        return instance.get();
    }

}
