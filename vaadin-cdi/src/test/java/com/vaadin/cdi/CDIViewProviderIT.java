/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import com.vaadin.navigator.View;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author adam-bien.com
 */
@RunWith(Arquillian.class)
public class CDIViewProviderIT {

    @Inject
    CDIViewProvider provider;

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "vaadincontext.jar").
                addClass(TestView.class).
                addPackage("com.vaadin.cdi").
                addAsManifestResource(new ByteArrayAsset(VaadinContext.class.getName().getBytes()),
                ArchivePaths.create("services/javax.enterprise.inject.spi.Extension")).
                addAsManifestResource(
                new ByteArrayAsset("<beans/>".getBytes()),
                ArchivePaths.create("beans.xml"));
    }

    @Test
    public void viewAvailableWithName() {
        View view = provider.getView(TestView.class.getSimpleName());
        assertNotNull(view);
    }
}
