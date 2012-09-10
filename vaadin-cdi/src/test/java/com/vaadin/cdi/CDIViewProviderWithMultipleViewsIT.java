package com.vaadin.cdi;

import static com.vaadin.cdi.ArchiveProvider.createJavaArchive;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.views.TestView;

/**
 * 
 * @author adam-bien.com
 */
@RunWith(Arquillian.class)
public class CDIViewProviderWithMultipleViewsIT {

    @Inject
    CDIViewProvider provider;

    @Deployment
    public static JavaArchive createTestArchive() {
        return createJavaArchive("com.vaadin.cdi.views");
    }

    @Test(expected = IllegalStateException.class)
    public void conventionalAndConfiguredNameCollision() {
        provider.getView(TestView.class.getSimpleName());
    }
}
