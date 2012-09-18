package com.vaadin.hellocdi.presentation.injectiontest;

import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@VaadinUIScoped
public class InjectableComponent extends CustomComponent {

    public InjectableComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        setCompositionRoot(layout);

        Label label = new Label("Injected component");
        layout.addComponent(label);
    }

}
