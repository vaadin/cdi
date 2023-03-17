/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import java.util.Collections;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.cdi.context.RouteScopedContext.NavigationData;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.router.Route;

@RunWith(CdiTestRunner.class)
public class RouteContextPseudoTest extends
        AbstractContextTest<RouteContextPseudoTest.RouteScopedTestBean> {

    @Override
    public void setUp() {
        // TODO Auto-generated method stub
        super.setUp();
    }

    @RouteScoped
    @Route("")
    public static class RouteScopedTestBean extends TestBean {
    }

    @Override
    protected Class<RouteScopedTestBean> getBeanType() {
        return RouteScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        // Intentionally UI Under Test Context. Nothing else needed.
        UIUnderTestContext context = new UIUnderTestContext() {

            @Override
            public void activate() {
                super.activate();

                NavigationData data = new NavigationData(
                        TestNavigationTarget.class, Collections.emptyList());
                ComponentUtil.setData(getUi(), NavigationData.class, data);
            }
        };

        return context;
    }

    @Override
    protected boolean isNormalScoped() {
        return false;
    }

}
