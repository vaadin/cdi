package com.vaadin.cdi;

import com.vaadin.cdi.internal.ConventionsAccessTest;
import com.vaadin.cdi.shiro.ShiroTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CDIIntegrationWithCustomDeployment.class,
		CDIUIProviderTest.class, ConventionsAccessTest.class, MultipleRootUIs.class,
		CDIIntegrationWithConflictingDeployment.class,
        CDIIntegrationWithConflictingUIPath.class,
        CDIIntegrationWithDefaultDeployment.class, RootViewAtContextRoot.class,
        MultipleAccessIsolation.class, ScopedInstances.class,
        InjectionTest.class, ConsistentInjectionTest.class,
        QualifiedInjection.class, MultipleSessionTest.class,
        ScopedProducer.class, CrossInjection.class, ShiroTest.class,
        InappropriateNestedServletInDeployment.class,
        InappropriateCDIViewInDeployment.class, NonPassivatingBeanTest.class })
public class DeploymentTestSuiteIT {

}
