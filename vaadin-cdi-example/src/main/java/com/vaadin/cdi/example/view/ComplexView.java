package com.vaadin.cdi.example.view;

import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(value = ComplexView.VIEW_ID)
public class ComplexView extends AbstractView {

    public static final String VIEW_ID = "complex";

    // These are new instance just for ComplexView, not created for the
    // (default) UI scope
    @Inject
    @New
    private InjectableLayout injected;

    @Inject
    @New
    private InjectableLayout injected2;

    @Override
    protected Component buildContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();

        Label label = new Label("This view has components injected in it.\n"
                + "The components are @Dependent, created just for this view.");
        label.setSizeUndefined();
        layout.addComponent(label);

        layout.addComponent(injected);
        layout.addComponent(injected2);

        return layout;
    }
}
