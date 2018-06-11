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
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.vaadin.cdi.BeanLookup.SERVICE;


/**
 * Default CDI instantiator.
 * <p>
 * Can be overridden by a @{@link VaadinServiceEnabled}
 * CDI Alternative/Specializes, or can be customized with a Decorator.
 *
 * @see Instantiator
 */
@VaadinServiceScoped
@VaadinServiceEnabled
public class CdiInstantiator implements Instantiator {

    private static final String CANNOT_USE_CDI_BEANS_FOR_I18_N
            = "Cannot use CDI beans for I18N, falling back to the default behavior.";
    private static final String FALLING_BACK_TO_DEFAULT_INSTANTIATION
            = "Falling back to default instantiation.";

    private AtomicBoolean i18NLoggingEnabled = new AtomicBoolean(true);
    private DefaultInstantiator delegate;
    @Inject
    private BeanManager beanManager;

    @Override
    public boolean init(VaadinService service) {
        if (delegate == null) {
            delegate = new DefaultInstantiator(service);
        }
        return delegate.init(service)
                && service instanceof CdiVaadinServletService;
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        return new BeanLookup<>(beanManager, type)
                .ifUnsatisfied(() ->
                        getLogger().debug("'{}' is not a CDI bean. "
                                + FALLING_BACK_TO_DEFAULT_INSTANTIATION, type.getName()))
                .ifAmbiguous(e ->
                        getLogger().debug("Multiple CDI beans found. "
                                + FALLING_BACK_TO_DEFAULT_INSTANTIATION, e))
                .fallbackTo(() -> {
                    final T instance = delegate.getOrCreate(type);
                    BeanProvider.injectFields(instance);
                    return instance;
                })
                .get();
    }

    @Override
    public I18NProvider getI18NProvider() {
        final BeanLookup<I18NProvider> lookup =
                new BeanLookup<>(beanManager, I18NProvider.class, SERVICE);
        if (i18NLoggingEnabled.compareAndSet(true, false)) {
            lookup
                    .ifUnsatisfied(() ->
                            getLogger().info("Can't find any bean implementing '{}'. "
                                            + CANNOT_USE_CDI_BEANS_FOR_I18_N,
                                    I18NProvider.class.getSimpleName()))
                    .ifAmbiguous(e ->
                            getLogger().warn("Found more beans for I18N. "
                                    + CANNOT_USE_CDI_BEANS_FOR_I18_N, e));
        } else {
            lookup.ifAmbiguous(e -> { });
        }
        return lookup
                .fallbackTo(delegate::getI18NProvider)
                .get();
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CdiInstantiator.class);
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        return Stream.concat(
                delegate.getServiceInitListeners(),
                Stream.of(beanManager::fireEvent));
    }

}
