package com.vaadin.hellocdi.presentation.sameobserver;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@VaadinUI
@VaadinUIScoped
public class ApplicationUI extends UI {

    @Inject
    @Named("ok")
    javax.enterprise.event.Event<String> okListeners;

    public ApplicationUI() {
        System.out.println("Instantiating new " + this);
    }

    @Override
    protected void init(WrappedRequest request) {
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponent(new Label("+ViewScopeTestUI works "));
        Button okButton = new Button("ok", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                okListeners.fire("Ok button clicked " + event);
            }
        });
        layout.addComponent(okButton);
        setContent(layout);

    }

    public void onOkButtonClicked(@Observes
    @Named("ok")
    String message) {
        System.out.println("Received: " + message);
    }

}
