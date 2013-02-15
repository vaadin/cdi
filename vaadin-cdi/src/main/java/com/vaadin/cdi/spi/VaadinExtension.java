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
package com.vaadin.cdi.spi;

import com.vaadin.cdi.internal.UIScopedContext;
import com.vaadin.cdi.internal.VaadinUIContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI extension that registers the Vaadin CDI scopes.
 */
public class VaadinExtension implements Extension {

    private static final Logger logger = Logger.getLogger(VaadinExtension.class.getCanonicalName());

    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {
        afterBeanDiscovery.addContext(new VaadinUIContext(beanManager));
        logger.log(Level.INFO, "{0} registered", VaadinUIContext.class.getSimpleName());
        afterBeanDiscovery.addContext(new UIScopedContext(beanManager));
        logger.log(Level.INFO, "{0} registered", UIScopedContext.class.getSimpleName());
    }
}
