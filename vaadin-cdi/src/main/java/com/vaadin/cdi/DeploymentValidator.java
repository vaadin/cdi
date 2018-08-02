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

import com.vaadin.cdi.annotation.NormalRouteScoped;
import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

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

import static com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode;
import static com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode.ABSENT_OWNER_OF_NON_ROUTE_COMPONENT;
import static com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode.NON_ROUTE_SCOPED_HAVE_OWNER;
import static com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode.NORMAL_SCOPED_COMPONENT;
import static com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode.OWNER_IS_NOT_ROUTE_COMPONENT;

@Dependent
class DeploymentValidator {

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
                if (type instanceof Class
                        && Component.class.isAssignableFrom((Class) type)) {
                    return true;
                }
            }
            return false;
        }

        private Optional<RouteScopeOwner> getRouteScopeOwner() {
            for (Annotation ann : bean.getQualifiers()) {
                if (ann instanceof RouteScopeOwner) {
                    return Optional.of((RouteScopeOwner) ann);
                }
            }
            return Optional.empty();
        }

    }

    /**
     * Represents a deployment problem to be passed to the container.
     * Message and stacktrace will appear in server log.
     * It is not thrown, or caught.
     */
    static class DeploymentProblem extends Throwable {

        enum ErrorCode {
            NORMAL_SCOPED_COMPONENT,
            NON_ROUTE_SCOPED_HAVE_OWNER,
            ABSENT_OWNER_OF_NON_ROUTE_COMPONENT,
            OWNER_IS_NOT_ROUTE_COMPONENT
        }

        private final Type baseType;
        private final ErrorCode errorCode;

        private DeploymentProblem(String message, Type baseType, ErrorCode errorCode) {
            super(message);
            this.baseType = baseType;
            this.errorCode = errorCode;
        }

        /**
         * For testing purposes only.
         *
         * @return annotated base type of the invalid bean
         */
        Type getBaseType() {
            return baseType;
        }

        /**
         * For testing purposes only.
         *
         * @return errorCode of the invalid bean
         */
        ErrorCode getErrorCode() {
            return errorCode;
        }
    }

    private class BeanValidator {

        private final BeanInfo beanInfo;
        private final Consumer<Throwable> problemConsumer;

        private BeanValidator(BeanInfo beanInfo, Consumer<Throwable> problemConsumer) {
            this.beanInfo = beanInfo;
            this.problemConsumer = problemConsumer;
        }

        private void validate() {
            Class<? extends Annotation> scope = beanInfo.getScope();
            Optional<RouteScopeOwner> owner = beanInfo.getRouteScopeOwner();
            Type baseType = beanInfo.getBaseType();

            if (beanInfo.isComponent() && beanManager.isNormalScope(scope)) {
                addProblem(NORMAL_SCOPED_COMPONENT,
                        "Normal scoped Vaadin components are not supported. " +
                        "'%s' should not belong to a normal scope.",
                        baseType.getTypeName()
                );
            }

            if (scope.equals(RouteScoped.class) || scope.equals(NormalRouteScoped.class)) {
                if (owner.isPresent()) {
                    if (isNonRouteComponent(owner.get().value())) {
                        addProblem(OWNER_IS_NOT_ROUTE_COMPONENT,
                                "'@%s' should define a route component on '%s'.",
                                RouteScopeOwner.class.getSimpleName(),
                                baseType.getTypeName()
                        );
                    }
                } else {
                    if (isNonRouteComponent(baseType)) {
                        addProblem(ABSENT_OWNER_OF_NON_ROUTE_COMPONENT,
                                "'%s' is not a route component, need a '@%s'.",
                                baseType.getTypeName(),
                                RouteScopeOwner.class.getSimpleName()
                        );
                    }
                }
            } else {
                if (owner.isPresent()) {
                    addProblem(NON_ROUTE_SCOPED_HAVE_OWNER,
                            "'%s' should be '@%s' or '@%s' to have a '@%s'.",
                            baseType.getTypeName(),
                            RouteScoped.class.getSimpleName(),
                            NormalRouteScoped.class.getSimpleName(),
                            RouteScopeOwner.class.getSimpleName()
                    );
                }
            }
        }

        private void addProblem(ErrorCode errorCode, String message, Object... param) {
            Type baseType = beanInfo.getBaseType();
            String desc = String.format(message, param);
            problemConsumer.accept(new DeploymentProblem(desc, baseType, errorCode));
        }

        private boolean isNonRouteComponent(Type type) {
            if (!(type instanceof Class)) {
                return true;
            }
            Class clazz = (Class) type;
            // RouteRegistry contains filtered route targets,
            // but we want to ignore filters here.
            return !clazz.isAnnotationPresent(Route.class)
                    && !HasErrorParameter.class.isAssignableFrom(clazz)
                    && !RouterLayout.class.isAssignableFrom(clazz);
        }

    }

    @Inject
    private BeanManager beanManager;

    void validate(Set<BeanInfo> infoSet, Consumer<Throwable> problemConsumer) {
        infoSet.forEach(info -> new BeanValidator(info, problemConsumer).validate());
    }

}
