/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.interceptor.Interceptor;

import org.apache.deltaspike.core.api.config.ConfigResolver;

/**
 * Works around a DeltaSpike + EAR classloading problem on Jakarta EE servers
 * (e.g. WildFly / JBoss EAP 8).
 * <p>
 * DeltaSpike's JMX {@code MBeanExtension} observes {@link BeforeBeanDiscovery}
 * and, during that callback, resolves the DeltaSpike ProjectStage via
 * {@link ConfigResolver#getConfigProvider()}. That method discovers its
 * {@code ConfigProvider} implementation with {@code ServiceLoader} against the
 * <em>thread context classloader</em>. When this add-on's WAR is nested inside
 * an EAR, the TCCL active during CDI bootstrap is the EAR (parent) classloader,
 * which cannot see {@code deltaspike-core-impl} in the WAR's {@code WEB-INF/lib}.
 * The lookup then finds no provider and deployment fails with
 * {@code java.lang.RuntimeException: Could not load ConfigProvider}.
 * <p>
 * This extension primes and caches the {@code ConfigProvider} early, while the
 * TCCL is set to this class's classloader (the WAR module classloader, which can
 * see {@code WEB-INF/lib}). {@link ConfigResolver} caches the provider in a
 * static field, so DeltaSpike's later lookup returns the cached instance and
 * never re-runs the failing {@code ServiceLoader} scan. DeltaSpike stays in the
 * WAR, so runtime bean resolution is unaffected.
 * <p>
 * Ordering: Weld instantiates all extensions (their constructors) before firing
 * {@link BeforeBeanDiscovery}, so the constructor below is guaranteed to run
 * before DeltaSpike's {@code MBeanExtension.init}. The high-priority observer is
 * a defensive fallback. The priming is a no-op cost (one cached lookup) for
 * standalone WAR deployments, where the problem does not occur.
 */
public class DeltaSpikeConfigBootstrap implements Extension {

    public DeltaSpikeConfigBootstrap() {
        primeConfigProvider();
    }

    void primeBeforeBeanDiscovery(
            @Observes @Priority(Interceptor.Priority.PLATFORM_BEFORE) BeforeBeanDiscovery beforeBeanDiscovery) {
        primeConfigProvider();
    }

    private static void primeConfigProvider() {
        final Thread thread = Thread.currentThread();
        final ClassLoader previous = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(
                    DeltaSpikeConfigBootstrap.class.getClassLoader());
            // Triggers (and caches) the ServiceLoader-based ConfigProvider
            // lookup now, with a TCCL that can see deltaspike-core-impl.
            ConfigResolver.getConfigProvider();
        } catch (RuntimeException e) {
            // Priming is best-effort: if it fails, fall through and let the
            // normal DeltaSpike code path run and surface the original error.
            getLogger().log(Level.FINE,
                    "Could not pre-initialize the DeltaSpike ConfigProvider; "
                            + "falling back to the default bootstrap.",
                    e);
        } finally {
            thread.setContextClassLoader(previous);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(DeltaSpikeConfigBootstrap.class.getCanonicalName());
    }

}
