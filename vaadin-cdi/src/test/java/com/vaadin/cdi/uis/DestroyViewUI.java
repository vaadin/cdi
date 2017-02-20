package com.vaadin.cdi.uis;

import com.vaadin.cdi.*;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;

@CDIUI("viewDestroy")
public class DestroyViewUI extends UI {
    public static final String CLOSE_BTN_ID = "close";
    public static final String LABEL_ID = "label";
    public static final String VIEW_DESTROY_COUNT_KEY = "viewcount";
    public static final String VIEWBEAN_DESTROY_COUNT_KEY = "viewbeancount";
    public static final String NAVIGATE_BTN_ID = "navigate";

    @Inject
    CDIViewProvider viewProvider;
    @Inject
    Counter counter;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        Button closeBtn = new Button("close UI");
        closeBtn.setId(CLOSE_BTN_ID);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        layout.addComponent(closeBtn);

        final Navigator navigator = new Navigator(this, new ViewDisplay() {
            @Override
            public void showView(View view) {
            }
        });
        navigator.addProvider(viewProvider);

        Button viewNavigateBtn = new Button("navigate");
        viewNavigateBtn.setId(NAVIGATE_BTN_ID);
        viewNavigateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo("other");
            }
        });
        layout.addComponent(viewNavigateBtn);

        setContent(layout);
    }

    @CDIView(value = "home")
    public static class HomeView implements View {
        @Inject
        ViewScopedBean viewScopedBean;

        @Inject
        Counter counter;

        @PreDestroy
        public void destroy() {
            counter.increment(VIEW_DESTROY_COUNT_KEY);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        }
    }

    @ViewScoped
    public static class ViewScopedBean implements Serializable {
        @Inject
        Counter counter;

        @PreDestroy
        public void destroy() {
            counter.increment(VIEWBEAN_DESTROY_COUNT_KEY);
        }


    }

    @CDIView("other")
    public static class OtherView implements View {

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {

        }
    }


}
