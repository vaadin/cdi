package com.vaadin.cdi;

import com.vaadin.cdi.uis.InstrumentedUI;
import com.vaadin.cdi.uis.InstrumentedView;
import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.arquillian.ajocado.Graphene.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunAsClient
@RunWith(Arquillian.class)
public class UIScopedContextIT {

    @Drone
    GrapheneSelenium firstWindow;

    @Drone
    GrapheneSelenium secondWindow;

    @ArquillianResource
    URL contextPath;

    protected IdLocator LABEL = id("label");

    @Deployment
    public static WebArchive deploy() {
        return ArchiveProvider.createWebArchive(InstrumentedUI.class,InstrumentedView.class);
    }

    @Before
    public void resetCounter() {
        InstrumentedUI.resetCounter();
    }

    @Test
    public void pageIsRenderedAndEmptyUICreatedAsManagedBean()
            throws MalformedURLException {
        openURI("instrumentedUI");
        assertTrue("InstrumentedUI should contain a label",
                firstWindow.isElementPresent(LABEL));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));
        // reset session
        firstWindow.restartBrowser();
        openURI("instrumentedUI");
        assertTrue("InstrumentedUI should contain a label",
                firstWindow.isElementPresent(LABEL));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));
    }

    private void openURI(String uri) throws MalformedURLException {
        firstWindow.open(new URL(contextPath.toString() + uri));
        waitModel.until(elementPresent.locator(LABEL));
    }

    @Test
    public void oneToOneRelationBetweenBrowserAndUI(){


    }

}
