package com.vaadin.cdi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.vaadin.cdi.internal.ConventionsTest;
import com.vaadin.cdi.shiro.ShiroTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CDIIntegrationWithCustomDeploymentTest.class,
        CDIUIProviderTest.class, ConventionsTest.class, MultipleRootUIsTest.class,
        CDIIntegrationWithConflictingDeploymentTest.class,
        CDIIntegrationWithConflictingUIPathTest.class,
        CDIIntegrationWithDefaultDeploymentTest.class, RootViewAtContextRootTest.class,
        MultipleAccessIsolationTest.class, ScopedInstancesTest.class,
        InjectionTest.class, ConsistentInjectionTest.class,
        QualifiedInjectionTest.class, MultipleSessionTest.class,
        ScopedProducerTest.class, CrossInjectionTest.class, ShiroTest.class,
        InappropriateNestedServletInDeploymentTest.class,
        InappropriateCDIViewInDeploymentTest.class, NonPassivatingBeanTest.class,
        UIDestroyTest.class})
public class DeploymentTestSuiteIT {

}
