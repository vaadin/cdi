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

import com.vaadin.cdi.uis.InstrumentedUI;
import com.vaadin.cdi.uis.InstrumentedView;

@RunAsClient
@RunWith(Arquillian.class)
public class CDIIntegrationWithVaadinIT {

    @Drone
    GrapheneSelenium firstWindow;

    @Drone
    GrapheneSelenium secondWindow;

    @ArquillianResource
    URL contextPath;

    protected IdLocator LABEL = id("label");
    protected IdLocator BUTTON = id("button");
    private final static String UI_URI = "instrumentedUI";
    private final static String VIEW_URI = UI_URI + "/#!instrumentedView";

    @Deployment
    public static WebArchive deploy() {
        return ArchiveProvider.createWebArchive(InstrumentedUI.class,
                InstrumentedView.class);
    }

    @Before
    public void resetCounter() {
        InstrumentedUI.resetCounter();
        InstrumentedView.resetCounter();
    }

    @Test
    public void pageIsRenderedAndEmptyUICreatedAsManagedBean()
            throws MalformedURLException {
        openFirstWindow(UI_URI);
        assertTrue("InstrumentedUI should contain a label",
                firstWindow.isElementPresent(LABEL));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));
        // reset session
        firstWindow.restartBrowser();
        openFirstWindow(UI_URI);
        assertTrue("InstrumentedUI should contain a label",
                firstWindow.isElementPresent(LABEL));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));
    }

    private void openFirstWindow(String uri) throws MalformedURLException {
        firstWindow.open(new URL(contextPath.toString() + uri));
        waitModel.until(elementPresent.locator(LABEL));
    }

    private void openSecondWindow(String uri) throws MalformedURLException {
        secondWindow.open(new URL(contextPath.toString() + uri));
        waitModel.until(elementPresent.locator(LABEL));
    }

    @Test
    public void oneToOneRelationBetweenBrowserAndUI()
            throws MalformedURLException {

        openFirstWindow(UI_URI);

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        int clickCount = number(firstWindow.getText(LABEL));
        assertThat(clickCount, is(1));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        clickCount = number(firstWindow.getText(LABEL));
        assertThat(clickCount, is(2));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(1));

        openSecondWindow(UI_URI);

        secondWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        clickCount = number(secondWindow.getText(LABEL));
        assertThat(clickCount, is(1));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));

        firstWindow.click(BUTTON);
        waitModel.waitForChange(retrieveText.locator(LABEL));
        clickCount = number(secondWindow.getText(LABEL));
        assertThat(clickCount, is(2));
        assertThat(InstrumentedUI.getNumberOfInstances(), is(2));

    }

    @Test
    public void anInjectedViewIsInstantiated() throws MalformedURLException {
        openFirstWindow(VIEW_URI);
        assertThat(InstrumentedView.getNumberOfInstances(), is(2));
    }

    public int number(String txt) {
        System.out.println("Text: " + txt);
        return Integer.parseInt(txt);
    }

}
