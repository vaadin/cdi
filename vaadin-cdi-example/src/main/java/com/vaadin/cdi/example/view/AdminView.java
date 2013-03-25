package com.vaadin.cdi.example.view;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.example.util.CounterService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(value = AdminView.VIEW_ID)
// if access control is configured on the server, can use
// @RolesAllowed(...)
public class AdminView extends AbstractView {

    public static final String VIEW_ID = "admin";

    // UI scoped
    @Inject
    private CounterService counterService;

    // This is a separate instance, not the default UI scoped one
    @Inject
    @Dependent
    @New
    private CounterService privateCounter;

    private Label uiScopedCountLabel = new Label();
    private Label privateCountLabel = new Label();

    @Override
    protected Component buildContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();

        Label label = new Label("Admin only content here");
        label.setSizeUndefined();
        layout.addComponent(label);

        uiScopedCountLabel.setSizeUndefined();
        layout.addComponent(uiScopedCountLabel);

        privateCountLabel.setSizeUndefined();
        layout.addComponent(privateCountLabel);

        Button incrementButton = new Button("Increment UI scoped");
        layout.addComponent(incrementButton);
        incrementButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                counterService.next();
                updateLabels();
            }
        });

        Button incrementButton2 = new Button("Increment private");
        layout.addComponent(incrementButton2);
        incrementButton2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                privateCounter.next();
                updateLabels();
            }
        });

        updateLabels();

        return layout;
    }

    private void updateLabels() {
        uiScopedCountLabel.setValue("UI scoped counter = "
                + counterService.get());
        privateCountLabel.setValue("Private counter = " + privateCounter.get());
    }
}
