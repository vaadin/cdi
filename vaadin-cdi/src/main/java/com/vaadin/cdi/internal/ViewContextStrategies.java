/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import com.vaadin.cdi.AfterViewChange;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByNavigation;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByNameAndParameters;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByName;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

/**
 * Holder class for ViewContextStrategy implementations.
 */
public class ViewContextStrategies {

    @NormalUIScoped
    @ViewContextByName
    public static class ViewName implements ViewContextStrategy {
        @Inject
        private CurrentViewState currentViewState;

        @Override
        public boolean inCurrentContext(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewState.getViewName());
        }
    }

    @NormalUIScoped
    @ViewContextByNameAndParameters
    public static class ViewNameAndParameters implements ViewContextStrategy {
        @Inject
        private CurrentViewState currentViewState;

        @Override
        public boolean inCurrentContext(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewState.getViewName())
                    && Objects.equals(parameters, currentViewState.getParameters());
        }
    }

    @NormalUIScoped
    @ViewContextByNavigation
    public static class EveryNavigation implements ViewContextStrategy {
        @Override
        public boolean inCurrentContext(String viewName, String parameters) {
            return false;
        }
    }

    @NormalUIScoped
    public static class CurrentViewState implements Serializable {
        private String viewName;
        private String parameters;

        private void onViewChange(@Observes @AfterViewChange ViewChangeEvent event) {
            viewName = event.getViewName();
            parameters = event.getParameters();
        }

        public String getViewName() {
            return viewName;
        }

        public String getParameters() {
            return parameters;
        }
    }
}
