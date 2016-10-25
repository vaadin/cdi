package com.vaadin.cdi;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.cdi.uis.AlternativeConventionUI;
import com.vaadin.cdi.uis.AlternativeConventionView;
import com.vaadin.cdi.uis.NavigatableAlternativeConventionUI;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

public class AlternativeConventionsTest extends AbstractManagedCDIIntegrationTest {

	@Deployment(name = "alternativeConventions")
	public static WebArchive initAndPostConstructAreConsistent() {
		URL resource = AlternativeConventionsTest.class.getResource("alternative-beans.xml");
		return ArchiveProvider.createWebArchive("alternativeConventions", false,
				NavigatableAlternativeConventionUI.class, AlternativeConventionUI.class,
				AlternativeConventionView.class)
				.addAsWebInfResource(resource, "beans.xml");
	}

	@Test
	@OperateOnDeployment("alternativeConventions")
	public void testUICustomConvention()
			throws MalformedURLException {
		openWindow(AlternativeConventionUI.class.getSimpleName());
		waitForValue(LABEL, AlternativeConventionUI.LABEL);
	}

	@Test
	@OperateOnDeployment("alternativeConventions")
	public void testViewCustomConvention()
			throws MalformedURLException {
		openWindow(
				NavigatableAlternativeConventionUI.class.getSimpleName() + "#!" + AlternativeConventionView.class.getSimpleName());
		waitForValue(LABEL, AlternativeConventionView.LABEL);
	}

}
