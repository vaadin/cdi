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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "child-no-owner", layout = ParentNoOwnerView.class)
public class ChildNoOwnerView extends Div {

    @Inject
    private Instance<BeanNoOwner> instance;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            RouterLink link = new RouterLink("parent", ParentNoOwnerView.class);
            link.setId("to-parent");
            add(link);

            Div div = new Div();
            div.setId("child-info");
            div.getElement().getStyle().set("display", "block");
            div.setText(instance.get().getData());
            add(div);

            NativeButton button = new NativeButton("Reset bean instance",
                    event -> div.setText(instance.get().getData()));
            add(button);
            button.setId("reset");
        }
    }
}
