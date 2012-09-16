package com.vaadin.hellocdi.presentation.uiwithview;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@VaadinUI
public class UIWithViewUI extends UI {

    @Inject
    HelloView helloView;

    @Override
    protected void init(WrappedRequest request) {
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponent(new Label("+UIWithViewUI works "));
        Button okButton = new Button("ok", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

            }
        });
        layout.addComponent(okButton);
        setContent(layout);

    }

}
