package com.vaadin.cdi;

import static com.vaadin.cdi.ArchiveProvider.createWebArchive;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.views.OneAndOnlyViewWithPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPath;
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
    public static WebArchive createTestArchive() {
        return createWebArchive(OneAndOnlyViewWithPath.class,
                OneAndOnlyViewWithoutPath.class);
    }

    @Test
    public void viewAvailableWithMapping() {
        View view = provider.getView("oneAndOnlyViewWithPath");
        assertNotNull(view);
    }

    @Test
    public void viewAvailableWithConvention() {
        View view = provider.getView("oneAndOnlyViewWithoutPath");
        assertNotNull(view);
    }
}
