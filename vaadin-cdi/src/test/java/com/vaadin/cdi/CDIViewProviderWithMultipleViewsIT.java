package com.vaadin.cdi;

import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static com.vaadin.cdi.ArchiveProvider.*;
import com.vaadin.cdi.views.TestView;
import com.vaadin.navigator.View;

/**
 * 
 * @author adam-bien.com
 */
@RunWith(Arquillian.class)
public class CDIViewProviderWithMultipleViewsIT {

    @Inject
    CDIViewProvider provider;

    @Deployment
    public static WebArchive createTestArchive() {
        return createWebArchive("com.vaadin.cdi.views");
    }

    @Test(expected = IllegalStateException.class)
    public void conventionalAndConfiguredNameCollision() {
        provider.getView(TestView.class.getSimpleName());
    }

    @Test
    public void notExistingView() {
        View view = provider.getView("should not exist");
        assertNull(view);
    }
}
