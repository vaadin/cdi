package com.vaadin.cdi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.vaadin.cdi.internal.ConventionsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CDIIntegrationWithCustomDeployment.class,
        CDIUIProviderTest.class, ConventionsTest.class,
        MultipleRootUIs.class, CDIIntegrationWithConflictingDeployment.class,
        CDIIntegrationWithConflictingUIPath.class,
        CDIIntegrationWithDefaultDeployment.class })
public class DeploymentTestSuiteIT {

}
