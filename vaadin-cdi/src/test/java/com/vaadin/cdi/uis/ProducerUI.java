/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.uis;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.Preferred;
import com.vaadin.cdi.internal.ProducedBean;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI
public class ProducerUI extends UI {

    public static final String BEAN2_ID = "bean2";

    public static final String BEAN1_ID = "bean1";

    public static final String WINDOW_OPEN_ID = "window-open";
    
    @Inject @Preferred ProducedBean bean1;
    
    @Inject @Preferred ProducedBean bean2;
    
    @Produces @Preferred @NormalUIScoped ProducedBean produceUIScopedBean() {
        return new ProducedBean("produced");
    }
    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+ProducerUI");
        label.setId("label");
        
        
        Label beanId1 = new Label(bean1.getId());
        beanId1.setId(BEAN1_ID);
        
        Label beanId2 = new Label(bean2.getId());
        beanId2.setId(BEAN2_ID);
        
        Button windowOpenButton = new Button("Open new window");
        windowOpenButton.setId(WINDOW_OPEN_ID);
        // Note: cannot use new BWO(Class) because it would use
        // BrowserWindowOpenerUIProvider, not CDIUIProvider
        new BrowserWindowOpener(request.getContextPath() + "/"
                + Conventions.deriveMappingForUI(ProducerUI.class))
                .extend(windowOpenButton);
        
        layout.addComponent(label);
        layout.addComponent(beanId1);
        layout.addComponent(beanId2);
        layout.addComponent(windowOpenButton);
        
        
        setContent(layout);
    }
}
