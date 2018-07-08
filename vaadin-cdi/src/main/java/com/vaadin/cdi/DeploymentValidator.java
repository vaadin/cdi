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

import com.vaadin.flow.component.Component;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Dependent
class DeploymentValidator {

    @Inject
    private BeanManager beanManager;

    void validate(Set<BeanInfo> infoSet, Consumer<Throwable> addDeploymentProblem) {
        infoSet.forEach(info -> validateBean(info).ifPresent(addDeploymentProblem));
    }

    private Optional<DeploymentProblem> validateBean(BeanInfo beanInfo) {
        if (!beanInfo.isComponent()) {
            return Optional.empty();
        }
        if (beanManager.isNormalScope(beanInfo.getScope())) {
            Type baseType = beanInfo.getBaseType();
            return Optional.of(new DeploymentProblem(String.format(
                    "Normal scoped Vaadin components are not supported. " +
                            "Should not belong to a normal scope: class '%s'",
                    baseType.getTypeName()), baseType));
        }
        return Optional.empty();
    }

    static class BeanInfo {

        private final Bean<?> bean;
        private final Annotated annotated;

        BeanInfo(Bean<?> bean, Annotated annotated) {
            this.bean = bean;
            this.annotated = annotated;
        }

        Type getBaseType() {
            return annotated.getBaseType();
        }

        private Class<? extends Annotation> getScope() {
            return bean.getScope();
        }

        private boolean isComponent() {
            for (Type type : bean.getTypes()) {
                if (type instanceof Class) {
                    if (Component.class.isAssignableFrom((Class) type)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    /**
     * Represents a deployment problem to be passed to the container.
     * Message and stacktrace will appear in server log.
     * It is not thrown, or caught.
     */
    static class DeploymentProblem extends Throwable {

        private final Type baseType;

        private DeploymentProblem(String message, Type baseType) {
            super(message);
            this.baseType = baseType;
        }

        /**
         * For testing purposes only.
         * @return annotated base type of the invalid bean
         */
        Type getBaseType() {
            return baseType;
        }

    }

}
