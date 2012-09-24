package com.vaadin.cdi;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
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
            UIBeanStore.class, VaadinCDIServlet.class, UIScopedContext.class,
            VaadinUIScoped.class };

    public static WebArchive createWebArchive(String packageName,
            Class... classes) {
        return createWebArchive(classes).addPackage(packageName);
    }

    public static WebArchive createWebArchive(Class... classes) {
        WebArchive archive = base();
        archive.addClasses(classes);
        System.out.println(archive.toString(true));
        return archive;
    }

    static WebArchive base() {
        MavenDependencyResolver resolver = DependencyResolvers.use(
                MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
        return ShrinkWrap
                .create(WebArchive.class, "vaadinextension.war")
                .addClasses(FRAMEWORK_CLASSES)
                .addAsLibraries(
                        resolver.artifact(
                                "com.vaadin:vaadin-server:7.0-SNAPSHOT")
                                .resolveAsFiles())
                .addAsLibraries(
                        resolver.artifact(
                                "com.vaadin:vaadin-shared:7.0-SNAPSHOT")
                                .resolveAsFiles())
                .addAsWebInfResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsWebInfResource(EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml"));

    }

    public static WebArchive createWebArchive(String packageName) {
        WebArchive archive = base();
        archive.addPackage(packageName);
        System.out.println(archive.toString(true));
        return archive;
    }

}
