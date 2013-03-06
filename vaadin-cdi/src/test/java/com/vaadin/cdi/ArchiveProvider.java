/*
 * Copyright 2012 Vaadin Ltd.
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
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import com.vaadin.cdi.access.ComponentTools;
import com.vaadin.cdi.access.JaasTools;
import com.vaadin.cdi.internal.BeanStoreContainer;
import com.vaadin.cdi.internal.CDIUIProvider;
import com.vaadin.cdi.internal.ContextDeployer;
import com.vaadin.cdi.internal.UIBeanStore;
import com.vaadin.cdi.internal.UIScopedContext;
import com.vaadin.cdi.internal.VaadinCDIServlet;
import com.vaadin.cdi.internal.VaadinExtension;

/**
 * 
 * @author adam-bien.com
 */
public class ArchiveProvider {

    public final static Class FRAMEWORK_CLASSES[] = new Class[] {
            ComponentTools.class, JaasTools.class, BeanStoreContainer.class,
            CDIUIProvider.class, CDIViewProvider.class, ContextDeployer.class,
            UIBeanStore.class, VaadinCDIServlet.class, UIScopedContext.class,
            VaadinUI.class };


    public static WebArchive createWebArchive(String warName,Class... classes) {
        WebArchive archive = base(warName);
        archive.addClasses(classes);
        System.out.println(archive.toString(true));
        return archive;
    }

    static WebArchive base(String warName) {
        MavenDependencyResolver resolver = DependencyResolvers.use(
                MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
        return ShrinkWrap
                .create(WebArchive.class,warName + ".war")
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

}
