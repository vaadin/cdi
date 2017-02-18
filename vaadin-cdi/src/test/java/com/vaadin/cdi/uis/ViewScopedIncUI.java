package com.vaadin.cdi.uis;

import com.vaadin.cdi.*;
import com.vaadin.cdi.internal.ClusterIncTestLayout;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.inject.Inject;

@CDIUI("viewscoped")
public class ViewScopedIncUI extends UI {

    public static final String NAVBTN_ID = "navigateBtn";

    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setSizeFull();

        navigator.init(this, layout);
    }

    @CDIView("")
    public static class RootView extends VerticalLayout implements View {

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            Button navBtn = new Button("navigate");
            navBtn.setId(NAVBTN_ID);
            navBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getUI().getNavigator().navigateTo("increment");
                }
            });
            addComponent(navBtn);
        }
    }

    @CDIView
    public static class IncrementView extends ClusterIncTestLayout implements View {

        @Inject
        IncrementBean incrementBean;
        @Inject
        NormalIncrementBean normalIncrementBean;

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            init(incrementBean, normalIncrementBean);
        }

    }

    @ViewScoped
    public static class IncrementBean extends ClusterIncTestLayout.IncTestBean {
    }

    @NormalViewScoped
    public static class NormalIncrementBean extends ClusterIncTestLayout.IncTestBean {
    }

}
