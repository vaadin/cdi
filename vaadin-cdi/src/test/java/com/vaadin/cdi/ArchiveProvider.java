package com.vaadin.cdi;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

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

    public static WebArchive createWebArchive(String packageName,
            Class... classes) {
        return ShrinkWrap
                .create(WebArchive.class, "vaadincontext.war")
                .addClasses(classes)
                .addClasses(FRAMEWORK_CLASSES)
                .addPackage(packageName)
                .addAsWebResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsWebResource(
                        new ByteArrayAsset("<beans/>".getBytes()),
                        ArchivePaths.create("beans.xml"));
    }

    public static WebArchive createWebArchive(Class... classes) {
        MavenDependencyResolver resolver = DependencyResolvers.use(
                MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
        WebArchive archive = ShrinkWrap
                .create(WebArchive.class, "vaadincontext.war")
                .addClasses(classes)
                .addClasses(FRAMEWORK_CLASSES)
                .addAsLibraries(
                        resolver.artifact(
                                "com.vaadin:vaadin-server:7.0-SNAPSHOT")
                                .resolveAsFiles())
                .addAsWebInfResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsWebInfResource(EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml"));
        System.out.println(archive.toString(true));
        return archive;
    }

    public static WebArchive createWebArchive(String packageName) {
        return ShrinkWrap
                .create(WebArchive.class, "vaadincontext.jar")
                .addPackage(packageName)
                .addClasses(FRAMEWORK_CLASSES)
                .addAsWebResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsWebResource(
                        new ByteArrayAsset("<beans/>".getBytes()),
                        ArchivePaths.create("beans.xml"));
    }

}
