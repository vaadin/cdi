package com.vaadin.cdi;

import com.vaadin.cdi.internal.ConventionsTest;
import com.vaadin.cdi.shiro.ShiroTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CDIIntegrationWithCustomDeploymentTest.class,
        CDIUIProviderTest.class, ConventionsTest.class, MultipleRootUIsTest.class,
        CDIIntegrationWithConflictingDeploymentTest.class,
        CDIIntegrationWithDefaultDeploymentTest.class, RootViewAtContextRootTest.class,
        MultipleAccessIsolationTest.class, ScopedInstancesTest.class,
        InjectionTest.class, ConsistentInjectionTest.class,
        QualifiedInjectionTest.class, MultipleSessionTest.class,
        ScopedProducerTest.class, CrossInjectionTest.class, ShiroTest.class,
        InappropriateDeploymentTest.class, NonPassivatingBeanTest.class,
        UIDestroyTest.class})
public class DeploymentTestSuiteIT {

}
