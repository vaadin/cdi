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

package com.vaadin.cdi;


import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import java.io.Serializable;
import java.util.Objects;

/**
 * Decision strategy whether target navigation state
 * belongs to active view context.
 * <p>
 * Implementations instantiated by CDI, so should have a scope.
 */
public interface ViewContextStrategy extends Serializable {

    /**
     * Whether active context contains target navigation state.
     *
     * @param viewName   target navigation view name
     * @param parameters target navigation parameters
     * @return true, to hold context open, false to release, and create a new context
     */
    boolean contains(String viewName, String parameters);

    /**
     * Strategy to hold the context open while
     * view name does not change.
     * <p>
     * This strategy is not on par with navigator view lifecycle.
     * While navigating to same view, same context remains active.
     * It means for example:
     * - {@link com.vaadin.navigator.View#enter(ViewChangeEvent)} will be called again
     * on the same view instance.
     * - Navigator view change events does not mean a view context change.
     */
    @NormalUIScoped
    class ViewName implements ViewContextStrategy {
        private String currentViewName;

        private void onViewChange(@Observes @AfterViewChange ViewChangeEvent event) {
            currentViewName = event.getViewName();
        }

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewName);
        }
    }

    /**
     * Strategy to hold the context open while
     * view name and view parameters does not change.
     * <p>
     * This strategy is on par with navigator view lifecycle.
     * - After all {@link ViewChangeEvent#beforeViewChange(ViewChangeEvent)}
     * is called - if navigation is not reverted -, new view context is activated.
     * - After all {@link ViewChangeEvent#afterViewChange(ViewChangeEvent)}
     * is called, old view context is closed.
     * - {@link com.vaadin.navigator.View#enter(ViewChangeEvent)} won't be called
     * again on the same view instance.
     */
    @NormalUIScoped
    class ViewNameAndParameters implements ViewContextStrategy {
        private String currentViewName;
        private String currentParameters;

        private void onViewChange(@Observes @AfterViewChange ViewChangeEvent event) {
            currentViewName = event.getViewName();
            currentParameters = event.getParameters();
        }

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewName)
                    && Objects.equals(parameters, currentParameters);
        }
    }

    /**
     * Strategy to release, and create a new context on every navigation
     * regardless of view name and parameters.
     * <p>
     * It is on par with navigator view lifecycle,
     * but navigating to same view with same parameters
     * triggers a navigation too.
     * <p>
     * In practice it works same as {@link ViewNameAndParameters},
     * even when parameters does not change.
     */
    @RequestScoped
    class Always implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
            return false;
        }
    }

}
