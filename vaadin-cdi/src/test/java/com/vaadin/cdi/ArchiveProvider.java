package com.vaadin.cdi;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import com.vaadin.cdi.component.ComponentTools;
import com.vaadin.cdi.component.JaasTools;

/**
 * 
 * @author adam-bien.com
 */
public class ArchiveProvider {

    public final static Class FRAMEWORK_CLASSES[] = new Class[] {
            ComponentTools.class, JaasTools.class, BeanStoreContainer.class,
            CDIUIProvider.class, CDIViewProvider.class, ContextDeployer.class,
            UIBeanStore.class, VaadinCDIServlet.class, VaadinContext.class,
            VaadinUI.class, VaadinUIAnnotation.class, VaadinUIScoped.class,
            VaadinViewAnnotation.class };

    public static JavaArchive createJavaArchive(String packageName,
            Class... classes) {
        return ShrinkWrap
                .create(JavaArchive.class, "vaadincontext.jar")
                .addClasses(classes)
                .addClasses(FRAMEWORK_CLASSES)
                .addPackage(packageName)
                .addAsManifestResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsManifestResource(
                        new ByteArrayAsset("<beans/>".getBytes()),
                        ArchivePaths.create("beans.xml"));
    }

    public static JavaArchive createJavaArchive(Class... classes) {
        return ShrinkWrap
                .create(JavaArchive.class, "vaadincontext.jar")
                .addClasses(classes)
                .addClasses(FRAMEWORK_CLASSES)
                .addAsManifestResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsManifestResource(
                        new ByteArrayAsset("<beans/>".getBytes()),
                        ArchivePaths.create("beans.xml"));
    }

    public static JavaArchive createJavaArchive(String packageName) {
        return ShrinkWrap
                .create(JavaArchive.class, "vaadincontext.jar")
                .addPackage(packageName)
                .addClasses(FRAMEWORK_CLASSES)
                .addAsManifestResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsManifestResource(
                        new ByteArrayAsset("<beans/>".getBytes()),
                        ArchivePaths.create("beans.xml"));
    }

}
