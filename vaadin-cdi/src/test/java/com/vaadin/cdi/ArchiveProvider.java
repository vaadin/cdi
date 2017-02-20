/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.cdi;

import javax.enterprise.inject.spi.Extension;

import com.vaadin.cdi.internal.*;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import com.vaadin.cdi.access.AccessControl;
import com.vaadin.cdi.access.JaasAccessControl;
import com.vaadin.cdi.server.VaadinCDIServlet;
import com.vaadin.cdi.server.VaadinCDIServletService;

/**
 */
public class ArchiveProvider {

    public final static Class FRAMEWORK_CLASSES[] = new Class[] {
            AccessControl.class, CDIUIProvider.class, CDIViewProvider.class,
            ContextDeployer.class, JaasAccessControl.class,
            UIScopedContext.class, CDIUI.class,
            ViewScopedContext.class,
            CDIView.class, VaadinSessionDestroyEvent.class,
            VaadinUICloseEvent.class, VaadinViewChangeEvent.class,
            VaadinViewCreationEvent.class, AbstractVaadinContext.class,
            VaadinViewChangeCleanupEvent.class, VaadinCDIServlet.class,
            VaadinCDIServletService.class,
            CDIUIProvider.DetachListenerImpl.class,
            CDIViewProvider.ViewChangeListenerImpl.class, Conventions.class,
            InconsistentDeploymentException.class, AnnotationUtil.class,
            VaadinExtension.class, VaadinContextualStorage.class, ContextWrapper.class,
            CDIUtil.class, URLMapping.class,
            UIScoped.class, ViewScoped.class, NormalUIScoped.class, NormalViewScoped.class,
            CounterFilter.class, Counter.class};
    public static WebArchive createWebArchive(String warName, Class... classes) {
        return createWebArchive(warName, true, classes);
    }

    public static WebArchive createWebArchive(String warName,
            boolean emptyBeansXml, Class... classes) {
        WebArchive archive = base(warName, emptyBeansXml);
        archive.addClasses(classes);
        System.out.println(archive.toString(true));
        return archive;
    }

    static WebArchive base(String warName, boolean emptyBeansXml) {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile(
                "pom.xml");
        // these version numbers should match the POM files
        WebArchive archive = ShrinkWrap
                .create(WebArchive.class, warName + ".war")
                .addClasses(FRAMEWORK_CLASSES)
                .addAsLibraries(
                        pom.resolve("com.vaadin:vaadin-server")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve("com.vaadin:vaadin-client-compiled")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve("com.vaadin:vaadin-themes")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve(
                                "org.apache.deltaspike.core:deltaspike-core-impl")
                                .withTransitivity().asFile())
                .addAsServiceProvider(Extension.class, VaadinExtension.class);
        if (emptyBeansXml) {
            archive = archive.addAsWebInfResource(EmptyAsset.INSTANCE,
                    ArchivePaths.create("beans.xml"));
        }
        return archive;
    }

}
