package com.vaadin.hellocdi.presentation.uiwithview;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@VaadinView("hello")
public class HelloView extends CustomComponent implements View {

    @PostConstruct
    public void initialize() {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TextField message = new TextField();
        message.setInputPrompt("User name");

        layout.addComponent(message);

        Button button = new Button("Login", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("Button clicked");
            }
        });

        layout.addComponent(message);
        layout.addComponent(button);

        setCompositionRoot(layout);

    }

    @Override
    public void enter(ViewChangeEvent event) {

    }

}
