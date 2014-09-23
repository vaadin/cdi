package com.vaadin.cdi.views;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.internal.UIScopedBean;
import com.vaadin.cdi.internal.ViewScopedBean;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@UIScoped
@CDIView(value = AbstractScopedInstancesView.UISCOPED_VIEW)
public class UIScopedView extends AbstractScopedInstancesView implements View {

    public static final String DESCRIPTION_LABEL = "label";
    public static final String INSTANCE_LABEL = "ui-instance";

    @Inject
    private UIScopedBean uiScopedBean;

    @Inject
    private ViewScopedBean viewScopedBean;

    @Override
    protected Component buildContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Label label = new Label("UIScopedView");
        label.setId(DESCRIPTION_LABEL);
        layout.addComponent(label);

        Label instanceLabel = new Label(String.valueOf(this));
        instanceLabel.setId(INSTANCE_LABEL);
        layout.addComponent(instanceLabel);

        final Label viewScopedLabel = new Label(String.valueOf(viewScopedBean
                .getUnderlyingInstance()));
        viewScopedLabel.setId(ViewScopedBean.ID);
        layout.addComponent(viewScopedLabel);

        final Label uiScopedLabel = new Label(String.valueOf(uiScopedBean
                .getUnderlyingInstance()));
        uiScopedLabel.setId(UIScopedBean.ID);
        layout.addComponent(uiScopedLabel);

        Button refreshButton = new Button("Refresh labels");
        refreshButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                uiScopedLabel.setValue(String.valueOf(uiScopedBean
                        .getUnderlyingInstance()));
                viewScopedLabel.setValue(String.valueOf(viewScopedBean
                        .getUnderlyingInstance()));
            }
        });
        layout.addComponent(refreshButton);

        return layout;
    }

}
