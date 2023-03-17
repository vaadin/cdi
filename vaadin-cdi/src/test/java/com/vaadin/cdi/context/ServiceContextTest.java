/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import com.vaadin.cdi.annotation.VaadinServiceScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@RunWith(CdiTestRunner.class)
public class ServiceContextTest extends AbstractContextTest<ServiceContextTest.ServiceScopedTestBean> {
    @Inject
    private BeanManager beanManager;

    @Override
    protected Class<ServiceScopedTestBean> getBeanType() {
        return ServiceScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new ServiceUnderTestContext(beanManager);
    }

    @Override
    protected boolean isNormalScoped() {
        return true;
    }

    @VaadinServiceScoped
    public static class ServiceScopedTestBean extends TestBean {
    }

}
