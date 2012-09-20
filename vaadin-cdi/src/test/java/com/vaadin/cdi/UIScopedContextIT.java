package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.arquillian.ajocado.Graphene.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

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

import com.vaadin.cdi.uis.EmptyUI;

@RunAsClient
@RunWith(Arquillian.class)
public class UIScopedContextIT {

    @Drone
    GrapheneSelenium driver;

    @ArquillianResource
    URL contextPath;

    protected IdLocator LABEL = id("label");

    @Deployment
    public static WebArchive deploy() {
        return ArchiveProvider.createWebArchive(EmptyUI.class);
    }

    @Before
    public void resetCounter() {
        EmptyUI.resetCounter();
    }

    @Test
    public void pageIsRenderedAndEmptyUICreatedAsManagedBean()
            throws MalformedURLException {
        openURI("emptyUI");
        assertTrue("EmptyUI should contain a label",
                driver.isElementPresent(LABEL));
        assertThat(EmptyUI.getNumberOfInstances(), is(1));
        // reset session
        driver.restartBrowser();
        openURI("emptyUI");
        assertTrue("EmptyUI should contain a label",
                driver.isElementPresent(LABEL));
        assertThat(EmptyUI.getNumberOfInstances(), is(2));
    }

    private void openURI(String uri) throws MalformedURLException {
        driver.open(new URL(contextPath.toString() + uri));
        waitModel.until(elementPresent.locator(LABEL));
    }

}
