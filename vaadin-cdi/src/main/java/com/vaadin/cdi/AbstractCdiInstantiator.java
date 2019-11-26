package com.vaadin.cdi;

import javax.enterprise.inject.spi.BeanManager;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;

abstract public class AbstractCdiInstantiator implements Instantiator {

    private static final String CANNOT_USE_CDI_BEANS_FOR_I18N = "Cannot use CDI beans for I18N, falling back to the default behavior.";
    private static final String FALLING_BACK_TO_DEFAULT_INSTANTIATION = "Falling back to default instantiation.";

    private AtomicBoolean i18NLoggingEnabled = new AtomicBoolean(true);
    private DefaultInstantiator delegate;

    public abstract Class<? extends VaadinService> getServiceClass();

    public abstract BeanManager getBeanManager();

    @Override
    public boolean init(VaadinService service) {
        delegate = new DefaultInstantiator(service);
        return delegate.init(service)
                && getServiceClass().isAssignableFrom(service.getClass());
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        return new BeanLookup<>(getBeanManager(), type)
                .setUnsatisfiedHandler(() -> getLogger().debug(
                        "'{}' is not a CDI bean. "
                                + FALLING_BACK_TO_DEFAULT_INSTANTIATION,
                        type.getName()))
                .setAmbiguousHandler(
                        e -> getLogger().debug(
                                "Multiple CDI beans found. "
                                        + FALLING_BACK_TO_DEFAULT_INSTANTIATION,
                                e))
                .lookupOrElseGet(() -> {
                    final T instance = delegate.getOrCreate(type);
                    BeanProvider.injectFields(instance);
                    return instance;
                });
    }

    @Override
    public I18NProvider getI18NProvider() {
        final BeanLookup<I18NProvider> lookup = new BeanLookup<>(
                getBeanManager(), I18NProvider.class, BeanLookup.SERVICE);
        if (i18NLoggingEnabled.compareAndSet(true, false)) {
            lookup.setUnsatisfiedHandler(() -> getLogger().info(
                    "Can't find any @VaadinServiceScoped bean implementing '{}'. "
                            + CANNOT_USE_CDI_BEANS_FOR_I18N,
                    I18NProvider.class.getSimpleName())).setAmbiguousHandler(
                            e -> getLogger().warn(
                                    "Found more beans for I18N. "
                                            + CANNOT_USE_CDI_BEANS_FOR_I18N,
                                    e));
        } else {
            lookup.setAmbiguousHandler(e -> {
            });
        }
        return lookup.lookupOrElseGet(delegate::getI18NProvider);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CdiInstantiator.class);
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        return Stream.concat(delegate.getServiceInitListeners(),
                Stream.of(getBeanManager()::fireEvent));
    }
}
