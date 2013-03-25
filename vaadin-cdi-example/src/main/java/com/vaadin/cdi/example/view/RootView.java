package com.vaadin.cdi.example.view;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.example.util.CounterService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView
public class RootView extends AbstractView {

    // UI scoped
    @Inject
    private CounterService counterService;

    @Override
    protected Component buildContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();

        Label label = new Label("Top-level content here");
        label.setSizeUndefined();
        layout.addComponent(label);

        final Label countLabel = new Label("UI scoped counter = "
                + counterService.get());
        countLabel.setSizeUndefined();
        layout.addComponent(countLabel);

        Button incrementButton = new Button("Increment UI scoped");
        layout.addComponent(incrementButton);
        incrementButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                countLabel.setValue("UI scoped counter = "
                        + counterService.next());
            }
        });

        return layout;
    }

}
