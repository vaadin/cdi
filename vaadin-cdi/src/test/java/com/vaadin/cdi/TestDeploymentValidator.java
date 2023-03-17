/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

@Alternative
@Dependent
class TestDeploymentValidator extends DeploymentValidator {

    @ApplicationScoped
    static class BeanInfoSetHolder {

        private Set<BeanInfo> infoSet;

        Set<BeanInfo> getInfoSet() {
            return Collections.unmodifiableSet(infoSet);
        }

        void setInfoSet(Set<BeanInfo> infoSet) {
            this.infoSet = infoSet;
        }

    }

    @Inject
    private BeanInfoSetHolder beanInfoSetHolder;

    @Override
    void validate(Set<BeanInfo> infoSet, Consumer<Throwable> problemConsumer) {
        // No-op.
        // We need CDI to startup in Unit tests even with
        // intentionally misconfigured beans.
        beanInfoSetHolder.setInfoSet(infoSet);
    }

    void validateForTest(Set<BeanInfo> infoSet, Consumer<Throwable> problemConsumer) {
        super.validate(infoSet, problemConsumer);
    }

}
