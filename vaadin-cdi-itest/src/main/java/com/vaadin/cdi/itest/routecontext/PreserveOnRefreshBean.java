/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import javax.annotation.PostConstruct;

import java.util.UUID;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;

@RouteScoped
@RouteScopeOwner(PreserveOnRefreshView.class)
public class PreserveOnRefreshBean extends AbstractCountedBean {

    @PostConstruct
    private void init() {
        setData(UUID.randomUUID().toString());
    }

}
