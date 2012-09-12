package com.vaadin.cdi;

import static com.vaadin.cdi.ArchiveProvider.createJavaArchive;
import com.vaadin.cdi.views.OneAndOnlyView;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.navigator.View;

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
        return createJavaArchive(OneAndOnlyView.class);
    }

    @Test
    public void viewAvailableWithName() {
        View view = provider.getView("oneAndOnlyView");
        assertNotNull(view);
    }
}
