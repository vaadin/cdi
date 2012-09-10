package com.vaadin.cdi;

import com.vaadin.cdi.views.TestView;
import com.vaadin.cdi.views.AnotherTestViewWithSameName;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static com.vaadin.cdi.ArchiveProvider.*;

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
        return createJavaArchive(CDIViewProvider.class,TestView.class,AnotherTestViewWithSameName.class);
    }

    @Test(expected=RuntimeException.class)
    public void conventionalAndConfiguredNameCollision() {
        provider.getView(TestView.class.getSimpleName());
    }
}
