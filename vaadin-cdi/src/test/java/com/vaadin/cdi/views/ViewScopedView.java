package com.vaadin.cdi.views;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.UIScopedBean;
import com.vaadin.cdi.internal.ViewScopedBean;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(value = AbstractScopedInstancesView.VIEWSCOPED_VIEW)
public class ViewScopedView extends AbstractScopedInstancesView implements View {

    public static final String DESCRIPTION_LABEL = "label";
    public static final String INSTANCE_LABEL = "view-instance";
    public static final String DESTROY_COUNT_LABEL = "destroy-count-label";

    /** number of times {@code @PreDestroy} method was called */
    private static final AtomicInteger destroyCount = new AtomicInteger();

    @Inject
    private UIScopedBean uiScopedBean;

    @Inject
    private ViewScopedBean viewScopedBean;

    @Override
    protected Component buildContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Label label = new Label("ViewScopedView");
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

        Label destroyCountLabel = new Label(String.valueOf(destroyCount.get()));
        destroyCountLabel.setId(ViewScopedView.DESTROY_COUNT_LABEL);
        layout.addComponent(destroyCountLabel);

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

    private Logger getLogger() {
        return Logger.getLogger(this.getClass().getCanonicalName());
    }

    @PreDestroy
    private void destroy() {
        destroyCount.getAndIncrement();
    }
}
