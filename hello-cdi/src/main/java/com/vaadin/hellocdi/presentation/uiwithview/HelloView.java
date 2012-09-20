package com.vaadin.hellocdi.presentation.uiwithview;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

@VaadinView("hello")
public class HelloView extends CustomComponent implements View {

    @PostConstruct
    public void initialize() {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TextField message = new TextField();
        message.setInputPrompt("User name");
        final Label output = new Label();
        output.setId("output");

        layout.addComponent(message);

        Button button = new Button("Login", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                output.setValue("Clicked");
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
