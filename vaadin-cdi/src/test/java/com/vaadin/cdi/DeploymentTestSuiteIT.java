package com.vaadin.cdi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.vaadin.cdi.internal.ConventionsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CDIIntegrationWithCustomDeployment.class,
        CDIUIProviderTest.class, ConventionsTest.class, MultipleRootUIs.class,
        CDIIntegrationWithConflictingDeployment.class,
        CDIIntegrationWithConflictingUIPath.class,
        CDIIntegrationWithDefaultDeployment.class, RootViewAtContextRoot.class,
        MultipleAccessIsolation.class, ScopedInstances.class,
        InjectionTest.class, ConsistentInjectionTest.class,
        QualifiedInjection.class })
public class DeploymentTestSuiteIT {

}
