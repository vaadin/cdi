/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;

import java.io.File;
import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public class ArchiveProvider {

    public static WebArchive createWebArchive(String warName,
            Consumer<WebArchive> customizer) {
        WebArchive archive = base(warName);
        customizer.accept(archive);
        return archive;
    }

    public static WebArchive createWebArchive(String warName,
            Class... classes) {
        return createWebArchive(warName,
                archive -> archive.addClasses(classes)
                        .addAsResource(new File("target/classes/META-INF")));
    }

    private static WebArchive base(String warName) {
        PomEquippedResolveStage pom = Maven.configureResolver().workOffline()
                .loadPomFromFile("target/effective-pom.xml");
        return ShrinkWrap.create(WebArchive.class, warName + ".war")
                .addAsLibraries(pom.resolve("com.vaadin:vaadin-cdi")
                        .withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.vaadin:flow-server")
                        .withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.vaadin:flow-client")
                        .withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.vaadin:flow-html-components")
                        .withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.vaadin:flow-polymer-template")
                        .withTransitivity().asFile())
                .addAsWebInfResource(EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml"))
                .addClasses(Counter.class, CounterFilter.class);
    }

}
