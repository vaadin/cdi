package com.vaadin.cdi.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractNavigatableView extends CustomComponent implements View {

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

    public AbstractNavigatableView() {
        super();
    }

    public AbstractNavigatableView(Component compositionRoot) {
        super(compositionRoot);
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

    protected abstract Component buildNavigationBar();

    protected Component buildNavigationBar(NavigationButton... buttons) {
        HorizontalLayout navBar = new HorizontalLayout();
        navBar.setSizeUndefined();

        for (NavigationButton button : buttons) {
            navBar.addComponent(button);
        }

        return navBar;
    }

}