package com.vaadin.cdi.example.view;

import javax.inject.Inject;

import com.vaadin.cdi.example.logging.LoggableEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView extends CustomComponent implements View {

    @Inject
    private javax.enterprise.event.Event<LoggableEvent> loggableEvent;

    protected static class NavigationButton extends Button implements
            Button.ClickListener {
        private String fragment;

        public NavigationButton(String caption, String fragment) {
            super(caption);
            this.fragment = fragment;
            addClickListener(this);
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
        // explicitly log the view change - this could also be done with an
        // interceptor or a decorator
        loggableEvent.fire(new LoggableEvent("Enter view ["
                + event.getViewName() + "]"));

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

        navBar.addComponent(new NavigationButton("Root view", null));
        navBar.addComponent(new NavigationButton("Complex view",
                ComplexView.VIEW_ID));
        navBar.addComponent(new NavigationButton("Event log view",
                EventLogView.VIEW_ID));
        navBar.addComponent(new NavigationButton("Admin view",
                AdminView.VIEW_ID));

        return navBar;
    }
}
