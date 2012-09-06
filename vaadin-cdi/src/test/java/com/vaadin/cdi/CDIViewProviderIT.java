/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import com.vaadin.navigator.View;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static com.vaadin.cdi.ArchiveProvider.*;

/**
 *
 * @author adam-bien.com
 */
@RunWith(Arquillian.class)
public class CDIViewProviderIT {

    @Inject
    CDIViewProvider provider;

    @Deployment
    public static JavaArchive createTestArchive() {
        return createJavaArchive(TestView.class);
    }

    @Test
    public void viewAvailableWithName() {
        View view = provider.getView(TestView.class.getSimpleName());
        assertNotNull(view);
    }
}
