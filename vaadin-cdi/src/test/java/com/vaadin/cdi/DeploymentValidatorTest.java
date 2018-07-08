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

import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CdiTestRunner.class)
public class DeploymentValidatorTest {

    @Inject
    private TestDeploymentValidator validator;

    @Inject
    private TestDeploymentValidator.BeanInfoSetHolder beanInfoSetHolder;

    @Test
    public void validate_multipleNormalScopedProblems_collected() {
        Set<DeploymentValidator.BeanInfo> infoSet = createBeanSet(
                PseudoScopedLabel.class,
                NormalScopedBean.class,
                NormalScopedLabel.class,
                ProducedNormalScopedComponent.class);

        List<Throwable> problems = new ArrayList<>();
        validator.validateForTest(infoSet, problems::add);

        assertEquals(2, problems.size());

        Set<Type> types = problems.stream()
                .map(info -> ((DeploymentValidator.DeploymentProblem) info).getBaseType())
                .collect(Collectors.toSet());

        assertTrue(types.contains(NormalScopedLabel.class));
        assertTrue(types.contains(ProducedNormalScopedComponent.class));
    }

    private Set<DeploymentValidator.BeanInfo> createBeanSet(Type... clazz) {
        Map<Type, DeploymentValidator.BeanInfo> map = getBeanInfoSetAsMap();
        return Arrays
                .stream(clazz)
                .map(map::get)
                .collect(Collectors.toSet());
    }

    private Map<Type, DeploymentValidator.BeanInfo> getBeanInfoSetAsMap() {
        return beanInfoSetHolder.getInfoSet().stream()
                // Weld causes duplicate key because of exposing weird things as Object.
                // Tests are not interested in it.
                .filter(beanInfo -> !beanInfo.getBaseType().equals(Object.class))
                .collect(Collectors.toMap(DeploymentValidator.BeanInfo::getBaseType, Function.identity()));
    }


    @NormalUIScoped
    public static class NormalScopedLabel extends Label {
    }

    @UIScoped
    public static class PseudoScopedLabel extends Label {
    }

    @NormalUIScoped
    public static class NormalScopedBean {
    }

    @Produces
    @NormalUIScoped
    private ProducedNormalScopedComponent getProducedComponent() {
        return new ProducedNormalScopedComponent();
    }

    @Vetoed
    public static class ProducedNormalScopedComponent extends Component {
    }

}
