/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.cdi.DeploymentValidator.BeanInfo;
import com.vaadin.cdi.DeploymentValidator.DeploymentProblem;
import com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode;
import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import static com.vaadin.cdi.DeploymentValidator.DeploymentProblem.ErrorCode.NORMAL_SCOPED_COMPONENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(CdiTestRunner.class)
public class DeploymentValidatorTest {

    @NormalUIScoped
    public static class NormalScopedLabel extends Label {
    }

    @UIScoped
    public static class PseudoScopedLabel extends Label {
    }

    @NormalUIScoped
    public static class NormalScopedBean {
    }

    @Vetoed
    public static class ProducedNormalScopedComponent extends Component {
    }

    @Route
    @RouteScoped
    @RouteScopeOwner(RouteTargetOfSelf.class)
    public static class RouteTargetOfSelf extends Label {
    }

    @Route
    @RouteScoped
    public static class TestRouteScopedTarget extends Label {
    }

    @RouteScoped
    public static class TestRouterLayout extends Label implements RouterLayout {
    }

    @RouteScoped
    public static class TestHasErrorParameter extends Label
            implements HasErrorParameter<NullPointerException> {
        @Override
        public int setErrorParameter(BeforeEnterEvent event,
                ErrorParameter<NullPointerException> parameter) {
            return 0;
        }
    }

    @RouteScopeOwner(RouteTargetOfSelf.class)
    @RouteScoped
    public static class BeanOfRouteTarget {
    }

    @RouteScopeOwner(TestRouterLayout.class)
    @RouteScoped
    public static class BeanOfRouterLayout {
    }

    @RouteScopeOwner(TestRouterLayout.class)
    @RouteScoped
    @Route(layout = TestRouterLayout.class)
    public static class RouteTargetOfRouterLayout {
    }

    @RouteScopeOwner(TestHasErrorParameter.class)
    @RouteScoped
    public static class BeanOfHasErrorParameter {
    }

    private static class ProblemId {
        private final ErrorCode errorCode;
        private final Type baseType;

        private ProblemId(DeploymentProblem problem) {
            this.errorCode = problem.getErrorCode();
            this.baseType = problem.getBaseType();
        }

        private ProblemId(ErrorCode errorCode, Type baseType) {
            this.errorCode = errorCode;
            this.baseType = baseType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ProblemId problemId = (ProblemId) o;
            return errorCode == problemId.errorCode
                    && Objects.equals(baseType, problemId.baseType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(errorCode, baseType);
        }

        @Override
        public String toString() {
            return "ProblemId{" + "errorCode=" + errorCode + ", baseType="
                    + baseType + '}';
        }
    }

    @Inject
    private TestDeploymentValidator validator;

    @Inject
    private TestDeploymentValidator.BeanInfoSetHolder beanInfoSetHolder;

    private List<Throwable> problems;

    @Before
    public void setUp() {
        problems = new ArrayList<>();
    }

    @After
    public void tearDown() {
        problems.forEach(problem -> getLogger().error(problem.getMessage()));
    }

    @Test
    public void validate_normalScopedProblems_collected() {
        Set<BeanInfo> infoSet = createBeanSet(PseudoScopedLabel.class,
                NormalScopedBean.class, NormalScopedLabel.class,
                ProducedNormalScopedComponent.class);

        validator.validateForTest(infoSet, problems::add);

        assertEquals(2, problems.size());
        assertProblemExists(NORMAL_SCOPED_COMPONENT, NormalScopedLabel.class);
        assertProblemExists(NORMAL_SCOPED_COMPONENT,
                ProducedNormalScopedComponent.class);
    }

    @Test
    public void validate_routeScopedProblems_collected() {
        Set<BeanInfo> infoSet = createBeanSet(RouteTargetOfSelf.class,
                TestRouteScopedTarget.class, TestRouterLayout.class,
                TestHasErrorParameter.class, BeanOfHasErrorParameter.class,
                BeanOfRouterLayout.class, BeanOfRouteTarget.class,
                RouteTargetOfRouterLayout.class);

        validator.validateForTest(infoSet, problems::add);

        assertEquals(0, problems.size());
    }

    private void assertProblemExists(ErrorCode errorCode, Type baseType) {
        ProblemId expected = new ProblemId(errorCode, baseType);
        boolean match = problems.stream()
                .map(info -> new ProblemId((DeploymentProblem) info))
                .anyMatch(Predicate.isEqual(expected));
        if (!match) {
            fail("Problem does not exist: " + expected);
        }
    }

    private Set<BeanInfo> createBeanSet(Type... clazz) {
        Map<Type, BeanInfo> map = getBeanInfoSetAsMap();
        return Arrays.stream(clazz).map(map::get).collect(Collectors.toSet());
    }

    private Map<Type, BeanInfo> getBeanInfoSetAsMap() {
        return beanInfoSetHolder.getInfoSet().stream()
                // Weld causes duplicate key because of exposing weird things as
                // Object.
                // Tests are not interested in it.
                .filter(beanInfo -> !beanInfo.getBaseType()
                        .equals(Object.class))
                .collect(Collectors.toMap(BeanInfo::getBaseType,
                        Function.identity()));
    }

    @Produces
    @NormalUIScoped
    private ProducedNormalScopedComponent getProducedComponent() {
        return new ProducedNormalScopedComponent();
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(DeploymentValidatorTest.class);
    }

}
