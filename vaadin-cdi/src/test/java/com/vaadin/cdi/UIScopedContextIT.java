package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.arquillian.ajocado.Graphene.elementPresent;
import static org.jboss.arquillian.ajocado.Graphene.id;
import static org.jboss.arquillian.ajocado.Graphene.waitModel;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.uis.EmptyUI;

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
        driver.open(new URL(contextPath.toString() + "emptyUI"));
        waitModel.until(elementPresent.locator(LABEL));
        assertTrue("EmptyUI should contain a label",
                driver.isElementPresent(LABEL));
        assertThat(EmptyUI.getNumberOfInstances(), is(1));
    }

}
