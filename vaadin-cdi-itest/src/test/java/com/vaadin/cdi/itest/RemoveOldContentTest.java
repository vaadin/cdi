package com.vaadin.cdi.itest;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.cdi.itest.regression.RemoveOldContentView;
import com.vaadin.testbench.TestBenchElement;

import static com.vaadin.cdi.itest.regression.RemoveOldContentView.NAVIGATE_BACK_FROM_ANOTHER_ROUTE_INSIDE_MAIN_LAYOUT_BUTTON_ID;
import static com.vaadin.cdi.itest.regression.RemoveOldContentView.NAVIGATE_BACK_FROM_ANOTHER_ROUTE_OUTSIDE_MAIN_LAYOUT_BUTTON_ID;
import static com.vaadin.cdi.itest.regression.RemoveOldContentView.NAVIGATE_TO_ANOTHER_ROUTE_INSIDE_MAIN_LAYOUT_BUTTON_ID;
import static com.vaadin.cdi.itest.regression.RemoveOldContentView.NAVIGATE_TO_ANOTHER_ROUTE_OUTSIDE_MAIN_LAYOUT_BUTTON_ID;
import static com.vaadin.cdi.itest.regression.RemoveOldContentView.SUB_LAYOUT_ID;

public class RemoveOldContentTest extends AbstractCdiTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("remove-old-content",
                webArchive -> webArchive
                        .addPackage(RemoveOldContentView.class.getPackage())
                        .addAsResource(new File("target/classes/META-INF")));
    }

    @Override
    protected String getTestPath() {
        return "/first-child-route";
    }

    @Test
    public void removeUIScopedRouterLayoutContent_navigateToAnotherRouteInsideMainLayoutAndBack_subLayoutOldContentRemoved() {
        open();
        waitForElementPresent(By.id(NAVIGATE_TO_ANOTHER_ROUTE_INSIDE_MAIN_LAYOUT_BUTTON_ID));
        click(NAVIGATE_TO_ANOTHER_ROUTE_INSIDE_MAIN_LAYOUT_BUTTON_ID);
        waitForElementPresent(By.id(
                NAVIGATE_BACK_FROM_ANOTHER_ROUTE_OUTSIDE_MAIN_LAYOUT_BUTTON_ID));
        click(
                NAVIGATE_BACK_FROM_ANOTHER_ROUTE_OUTSIDE_MAIN_LAYOUT_BUTTON_ID);
        waitForElementPresent(By.id(SUB_LAYOUT_ID));

        assertSubLayoutHasNoOldContent();
    }

    @Test
    public void removeUIScopedRouterLayoutContent_navigateToAnotherRouteOutsideMainLayoutAndBack_mainLayoutOldContentRemoved() {
        open();
        waitForElementPresent(By.id(NAVIGATE_TO_ANOTHER_ROUTE_OUTSIDE_MAIN_LAYOUT_BUTTON_ID));
        click(NAVIGATE_TO_ANOTHER_ROUTE_OUTSIDE_MAIN_LAYOUT_BUTTON_ID);
        waitForElementPresent(By.id(
                NAVIGATE_BACK_FROM_ANOTHER_ROUTE_INSIDE_MAIN_LAYOUT_BUTTON_ID));
        click(NAVIGATE_BACK_FROM_ANOTHER_ROUTE_INSIDE_MAIN_LAYOUT_BUTTON_ID);
        waitForElementPresent(By.id(SUB_LAYOUT_ID));

        assertSubLayoutHasNoOldContent();
    }

    private void assertSubLayoutHasNoOldContent() {
        TestBenchElement subLayout = $("div").id(SUB_LAYOUT_ID);
        List<WebElement> subLayoutChildren = subLayout
                .findElements(By.tagName("div"));
        Assert.assertEquals(1, subLayoutChildren.size());
    }
}
