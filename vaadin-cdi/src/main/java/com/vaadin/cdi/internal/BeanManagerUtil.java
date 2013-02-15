/*
 * Copyright 2013 Vaadin Ltd.
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
package com.vaadin.cdi.internal;

import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Utility classes for working with a {@link BeanManager}.
 */
public final class BeanManagerUtil {

    private final BeanManager beanManager;

    BeanManagerUtil(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    UIBeanStoreContainer getSessionBoundBeanStoreContainer() {
        Set<Bean<?>> beans = beanManager.getBeans(UIBeanStoreContainer.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "No bean store container bound to session");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean store container bound to session");
        }

        Bean<?> bean = beans.iterator().next();
        return (UIBeanStoreContainer) beanManager.getReference(bean,
                bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }

    <T> Bean<T> findBeanOfType(Class<?> beanClass) {
        final Set<Bean<?>> beans = beanManager.getBeans(beanClass);

        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "No bean of type " + beanClass.getCanonicalName() + " was found");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean of type " + beanClass.getCanonicalName() + " was found");
        }

        return (Bean<T>) beans.iterator().next();
    }
}
