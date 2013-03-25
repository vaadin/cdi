package com.vaadin.cdi.example.view;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.example.util.CounterService;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

// Components should not be shared between multiple parents.
// This is one way to ensure that. Another option would be to use named instances.
@Dependent
public class InjectableLayout extends VerticalLayout {

    // Inject a new instance of a component.
    // A component instance cannot be used in multiple layouts, but a named
    // instance could be made available to another class for other use.
    @Inject
    @New
    private Label label;

    // An alternative to this would be injection with @Named.
    @Inject
    @UIScoped
    private CounterService layoutCounter;

    public InjectableLayout() {
        setSizeUndefined();
    }

    @PostConstruct
    private void initLayout() {
        addComponent(new Label("Injected layout " + layoutCounter.next()));

        label.setValue("Injected label in an injected layout");
        addComponent(label);
    }
}
