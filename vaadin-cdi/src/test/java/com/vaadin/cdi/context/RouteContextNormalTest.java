/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.context;

import java.io.Serializable;
import java.util.Collections;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.vaadin.cdi.annotation.NormalRouteScoped;
import com.vaadin.cdi.context.RouteScopedContext.NavigationData;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.startup.ApplicationConfiguration;

import static com.vaadin.cdi.SerializationUtils.serializeAndDeserialize;
import static org.junit.Assert.assertNotNull;

@RunWith(CdiTestRunner.class)
public class RouteContextNormalTest extends
        AbstractContextTest<RouteContextNormalTest.RouteScopedTestBean> {

    @NormalRouteScoped
    @Route("")
    public static class RouteScopedTestBean extends TestBean implements Serializable {
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
        return true;
    }

    @Test
    public void activeContext_UISerializable() throws Exception {
        UIUnderTestContext context = (UIUnderTestContext) createContext();
        context.activate();
        BeanProvider.getContextualReference(getBeanType());
        UI ui = context.getUi();
        try (MockedStatic<ApplicationConfiguration> appCfg = Mockito.mockStatic(ApplicationConfiguration.class)) {
            appCfg.when(() -> ApplicationConfiguration.get(ArgumentMatchers.any()))
                    .thenReturn(Mockito.mock(ApplicationConfiguration.class));
            UI ui2 = serializeAndDeserialize(ui);
            assertNotNull(ui2);
        }
    }

}
