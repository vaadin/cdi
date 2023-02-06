/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi.itest;

import java.io.File;
import java.util.function.Consumer;

import org.jboss.arquillian.container.test.api.BeforeDeployment;
import org.jboss.shrinkwrap.api.Archive;
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
        WebArchive archive = ShrinkWrap.create(WebArchive.class, warName + ".war")
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
        return applyPayaraSlf4jWorkaround(archive, pom);
    }


    // Workaround for https://github.com/payara/Payara/issues/5898
    // Slf4J implementation lookup error in Payara 6
    private static WebArchive applyPayaraSlf4jWorkaround(WebArchive archive, PomEquippedResolveStage pom) {
        if ("payara".equals(System.getProperty("arquillian.launch"))) {
            archive.addAsWebInfResource(AbstractCdiTest.class.getClassLoader().getResource("payara/glassfish-web.xml"),
                            "glassfish-web.xml")
                    .addAsLibraries(pom.resolve("org.slf4j:slf4j-simple").withoutTransitivity().asFile());

        }
        return archive;
    }

}
