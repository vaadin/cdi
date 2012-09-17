package com.vaadin.hellocdi.presentation.namedevents;

import javax.inject.Inject;
import javax.inject.Named;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@VaadinUI
@VaadinUIScoped
public class NamedEventsUI extends UI{

    @Inject @Named("ok")
    javax.enterprise.event.Event<String> okListeners;
    
    @Override
    protected void init(WrappedRequest request) {
        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponent(new Label("+ViewScopeTestUI works "));
        Button okButton = new Button("ok",new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
              okListeners.fire("Ok button clicked " + event);
            }
        });
        layout.addComponent(okButton);
        setContent(layout);
        
    }

}
