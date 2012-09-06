/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 *
 * @author adam-bien.com
 */
public class ArchiveProvider {
    public static JavaArchive createJavaArchive(Class ...classes){
    return ShrinkWrap.create(JavaArchive.class, "vaadincontext.jar").
                addClasses(classes).
                addPackage("com.vaadin.cdi").
                addAsManifestResource(new ByteArrayAsset(VaadinContext.class.getName().getBytes()),
                ArchivePaths.create("services/javax.enterprise.inject.spi.Extension")).
                addAsManifestResource(
                new ByteArrayAsset("<beans/>".getBytes()),
                ArchivePaths.create("beans.xml"));
    }
    
}
