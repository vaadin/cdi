package com.vaadin.cdi.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractNavigatableView extends CustomComponent implements
        View {

    public static final String UISCOPED_VIEW = "uiscoped";
    public static final String VIEWSCOPED_VIEW = "";

    public static final String NAVIGATE_TO_UISCOPED = "navigate-uiscoped";
    public static final String NAVIGATE_TO_VIEWSCOPED = "navigate-viewscoped";

    protected static class NavigationButton extends Button implements
            Button.ClickListener {
        private String fragment;

        public NavigationButton(String caption, String fragment, String id) {
            super(caption);
            this.fragment = fragment;
            addClickListener(this);
            setId(id);
        }

        @Override
        public void buttonClick(ClickEvent event) {
            // go to #!fragment, or remove fragment if null
            if (fragment != null) {
                Page.getCurrent().setUriFragment("!" + fragment);
            } else {
                // using dummy fragment because of #11312
                Page.getCurrent().setUriFragment("!");
            }
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        Component navigationBar = buildNavigationBar();
        mainLayout.addComponent(navigationBar);
        mainLayout.setExpandRatio(navigationBar, 0);

        Component content = buildContent();
        mainLayout.addComponent(content);
        mainLayout.setExpandRatio(content, 1);

        setCompositionRoot(mainLayout);
    }

    protected abstract Component buildContent();

    protected Component buildNavigationBar() {
        HorizontalLayout navBar = new HorizontalLayout();
        navBar.setSizeUndefined();

        navBar.addComponent(new NavigationButton("ViewScoped", VIEWSCOPED_VIEW,
                NAVIGATE_TO_VIEWSCOPED));
        navBar.addComponent(new NavigationButton("UIScoped", UISCOPED_VIEW,
                NAVIGATE_TO_UISCOPED));

        return navBar;
    }

}
