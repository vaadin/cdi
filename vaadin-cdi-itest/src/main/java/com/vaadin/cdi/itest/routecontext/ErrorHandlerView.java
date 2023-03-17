/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.routecontext;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLink;

@RouteScoped
@RouteScopeOwner(ErrorParentView.class)
@ParentLayout(ErrorParentView.class)
public class ErrorHandlerView extends AbstractCountedView
        implements HasErrorParameter<CustomException> {

    public static final String PARENT = "parent";

    @Inject
    @RouteScopeOwner(ErrorHandlerView.class)
    private Instance<CustomExceptionSubButton> buttonInjection;

    @Inject
    @RouteScopeOwner(ErrorHandlerView.class)
    private Instance<CustomExceptionSubDiv> divInjection;

    private boolean isSubDiv;

    private Component current;

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<CustomException> parameter) {
        add(new RouterLink(PARENT, ErrorParentView.class));

        NativeButton button = new NativeButton("switch content", ev -> {
            remove(current);
            if (isSubDiv) {
                current = buttonInjection.get();
            } else {
                current = divInjection.get();
            }
            add(current);
            isSubDiv = !isSubDiv;
        });
        button.setId("switch-content");
        add(button);
        current = buttonInjection.get();
        add(current);

        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

}
