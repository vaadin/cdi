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

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import com.vaadin.cdi.access.AccessControl;
import com.vaadin.cdi.access.JaasAccessControl;
import com.vaadin.cdi.internal.AbstractVaadinContext;
import com.vaadin.cdi.internal.ContextDeployer;
import com.vaadin.cdi.internal.UIBean;
import com.vaadin.cdi.internal.UIScopedContext;
import com.vaadin.cdi.internal.VaadinCDIServlet;
import com.vaadin.cdi.internal.VaadinCDIServletService;
import com.vaadin.cdi.internal.VaadinExtension;
import com.vaadin.cdi.internal.VaadinViewChangeCleanupEvent;
import com.vaadin.cdi.internal.VaadinSessionDestroyEvent;
import com.vaadin.cdi.internal.VaadinUICloseEvent;
import com.vaadin.cdi.internal.VaadinViewChangeEvent;
import com.vaadin.cdi.internal.VaadinViewCreationEvent;
import com.vaadin.cdi.internal.ViewBean;
import com.vaadin.cdi.internal.ViewScopedContext;

/**
 */
public class ArchiveProvider {

    public final static Class FRAMEWORK_CLASSES[] = new Class[] {
            AccessControl.class, CDIUIProvider.class, CDIViewProvider.class,
            ContextDeployer.class, JaasAccessControl.class, UIBean.class,
            UIScopedContext.class, CDIUI.class, ViewBean.class,
            ViewScopedContext.class, CDIView.class,
            VaadinSessionDestroyEvent.class, VaadinUICloseEvent.class,
            VaadinViewChangeEvent.class, VaadinViewCreationEvent.class,
            AbstractVaadinContext.class,
            VaadinViewChangeCleanupEvent.class, VaadinCDIServlet.class,
            VaadinCDIServletService.class,
            VaadinCDIServletService.SessionListenerImpl.class,
            CDIUIProvider.DetachListenerImpl.class,
            CDIViewProvider.ViewChangeListenerImpl.class };

    public static WebArchive createWebArchive(String warName, Class... classes) {
        WebArchive archive = base(warName);
        archive.addClasses(classes);
        System.out.println(archive.toString(true));
        return archive;
    }

    static WebArchive base(String warName) {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
        return ShrinkWrap
                .create(WebArchive.class, warName + ".war")
                .addClasses(FRAMEWORK_CLASSES)
                .addAsLibraries(
                        pom.resolve("com.vaadin:vaadin-server:7.2.6")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve("com.vaadin:vaadin-themes:7.2.6")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve(
                                "org.apache.deltaspike.core:deltaspike-core-impl:1.0.1")
                                .withTransitivity().asFile())
                .addAsWebInfResource(
                        new ByteArrayAsset(VaadinExtension.class.getName()
                                .getBytes()),
                        ArchivePaths
                                .create("services/javax.enterprise.inject.spi.Extension"))
                .addAsWebInfResource(EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml"));

    }

}
