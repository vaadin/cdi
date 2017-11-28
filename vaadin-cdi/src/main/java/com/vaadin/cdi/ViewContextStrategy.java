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

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * Decision strategy on whether target navigation state belongs to active view
 * context. When the target navigation state does not belong to the active view
 * context, the current context will be released and a new one is created.
 * <p>
 * By default the views are using the {@link Dependent} scope, which can be used
 * but is not recommended. Any {@link View} with a {@code ViewContextStrategy}
 * should use one of the scopes provided in the Vaadin CDI integration.
 */
public interface ViewContextStrategy extends Serializable {

    /**
     * Returns whether the active context contains target navigation state. This
     * method should compare the current navigation state and the one given
     * through the parameters and decide if the current context should be held
     * open or released.
     *
     * @param viewName
     *            target navigation view name
     * @param parameters
     *            target navigation parameters
     * @return {@code true} to hold context open; {@code false} to release it
     */
    boolean contains(String viewName, String parameters);

    /**
     * Strategy to hold the context open while view name does not change.
     * <p>
     * This strategy is not on par with navigator view life cycle. While
     * navigating to same view, same context remains active.
     * {@link com.vaadin.navigator.View#enter(ViewChangeEvent)} will be called
     * again on the same view instance.
     * <p>
     * <strong>Note:</strong> Navigator view change events do not mean that the
     * view context has changed.
     */
    @NormalUIScoped
    class ViewName implements ViewContextStrategy {
        private String currentViewName;

        private void onViewChange(
                @Observes @AfterViewChange ViewChangeEvent event) {
            currentViewName = event.getViewName();
        }

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewName);
        }
    }

    /**
     * Strategy to hold the context open while view name and view parameters do
     * not change.
     * <p>
     * This strategy is on par with navigator view life cycle. If navigation is
     * not reverted in a
     * {@link ViewChangeEvent#beforeViewChange(ViewChangeEvent)}, a new view
     * context is activated. After
     * {@link ViewChangeEvent#afterViewChange(ViewChangeEvent)} is called, old
     * view context will be closed.
     * <p>
     * {@link View#enter(ViewChangeEvent)} will be called for the new
     * {@link View} instance.
     */
    @NormalUIScoped
    class ViewNameAndParameters implements ViewContextStrategy {
        private String currentViewName;
        private String currentParameters;

        private void onViewChange(
                @Observes @AfterViewChange ViewChangeEvent event) {
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
     * It is on par with navigator view life cycle, but navigating to same view
     * with same parameters releases the context and creates a new one.
     * <p>
     * In practice it works same as {@link ViewNameAndParameters}, even when
     * parameters does not change.
     */
    @NormalUIScoped
    class Always implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
            return false;
        }
    }

}
