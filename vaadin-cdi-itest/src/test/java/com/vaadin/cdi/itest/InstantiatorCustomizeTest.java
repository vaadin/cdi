/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest;


import com.vaadin.cdi.itest.instantiatorcustomize.InstantiatorAlternative;
import com.vaadin.cdi.itest.instantiatorcustomize.InstantiatorCustomizeView;
import com.vaadin.cdi.itest.instantiatorcustomize.InstantiatorDecorator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.vaadin.cdi.itest.instantiatorcustomize.InstantiatorCustomizeView.CUSTOMIZED;
import static com.vaadin.cdi.itest.instantiatorcustomize.InstantiatorCustomizeView.VIEW;

@SuppressWarnings("ArquillianTooManyDeployment")
public class InstantiatorCustomizeTest extends AbstractCdiTest {

    @Deployment(name = "decorator", testable = false)
    public static WebArchive decoratorDeployment() {
        return ArchiveProvider.createWebArchive("instantiator-decorator-test",
                InstantiatorDecorator.class,
                InstantiatorCustomizeView.class);
    }

    @Deployment(name = "alternative", testable = false)
    public static WebArchive alternativeDeployment() {
        return ArchiveProvider.createWebArchive("instantiator-alternative-test",
                InstantiatorAlternative.class,
                InstantiatorCustomizeView.class);
    }

    @Before
    public void setUp() {
        open();
    }

    @Test
    @OperateOnDeployment("decorator")
    public void instantiatorCustomizedByDecorator() {
        assertInstantiatorCustomized();
    }

    @Test
    @OperateOnDeployment("alternative")
    public void instantiatorCustomizedByAlternative() {
        assertInstantiatorCustomized();
    }

    private void assertInstantiatorCustomized() {
        Assert.assertEquals(CUSTOMIZED, $("div").id(VIEW).getText());
    }

}
