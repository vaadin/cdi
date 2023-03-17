/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import com.vaadin.cdi.annotation.NormalUIScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class UiContextNormalTest extends AbstractContextTest<UiContextNormalTest.NormalUIScopedTestBean> {

    @Override
    protected Class<NormalUIScopedTestBean> getBeanType() {
        return NormalUIScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new UIUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return true;
    }

    @NormalUIScoped
    public static class NormalUIScopedTestBean extends TestBean {
    }

}
