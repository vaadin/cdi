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

package com.vaadin.cdi.integrationtests;

import com.vaadin.cdi.Root;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.component.ComponentTools;
import com.vaadin.cdi.component.JaasTools;
import com.vaadin.cdi.deploy.Deployer;
import com.vaadin.cdi.deploy.InconsistentDeploymentException;
import com.vaadin.cdi.internal.BeanManagerUtil;
import com.vaadin.cdi.internal.BeanStore;
import com.vaadin.cdi.internal.CDIUIProvider;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.UIBeanStoreContainer;
import com.vaadin.cdi.internal.UIScopedContext;
import com.vaadin.cdi.internal.VaadinCDIServlet;
import com.vaadin.cdi.internal.VaadinUIBean;
import com.vaadin.cdi.internal.VaadinUIContext;
import com.vaadin.cdi.spi.VaadinExtension;
import com.vaadin.cdi.view.CDIViewProvider;
import com.vaadin.cdi.view.VaadinView;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;


public class ArchiveProvider {

    public final static Class FRAMEWORK_CLASSES[] = new Class[] {
            ComponentTools.class,
            JaasTools.class, 
            Deployer.class,
            InconsistentDeploymentException.class,
            BeanManagerUtil.class,
            BeanStore.class,
            CDIUIProvider.class,
            Conventions.class,
            UIBeanStoreContainer.class,
            UIScopedContext.class,
            VaadinCDIServlet.class,
            VaadinUIBean.class,
            VaadinUIContext.class,
            VaadinExtension.class,
            Root.class,
            UIScoped.class,
            VaadinUI.class,
            // TODO View classes
    };


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
                                "com.vaadin:vaadin-server:7.0.0")
                                .resolveAsFiles())
                .addAsLibraries(
                        resolver.artifact(
                                "com.vaadin:vaadin-shared:7.0.0")
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
