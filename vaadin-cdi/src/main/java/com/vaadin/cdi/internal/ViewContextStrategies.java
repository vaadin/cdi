/*
 * Copyright 2000-2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.vaadin.cdi.internal;

import com.vaadin.cdi.AfterViewChange;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.cdi.viewcontextstrategy.EveryNavigationDriven;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.cdi.viewcontextstrategy.ViewNameAndParametersDriven;
import com.vaadin.cdi.viewcontextstrategy.ViewNameDriven;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

/**
 * Holder class for ViewContextStrategy implementations.
 */
public class ViewContextStrategies {

    @NormalUIScoped
    @ViewNameDriven
    public static class ViewName implements ViewContextStrategy {
        @Inject
        private CurrentViewState currentViewState;

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewState.getViewName());
        }
    }

    @NormalUIScoped
    @ViewNameAndParametersDriven
    public static class ViewNameAndParameters implements ViewContextStrategy {
        @Inject
        private CurrentViewState currentViewState;

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewState.getViewName())
                    && Objects.equals(parameters, currentViewState.getParameters());
        }
    }

    @NormalUIScoped
    @EveryNavigationDriven
    public static class EveryNavigation implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
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
