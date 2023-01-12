package com.vaadin.cdi;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;

@EnableWeld
public class AbstractWeldTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

}
