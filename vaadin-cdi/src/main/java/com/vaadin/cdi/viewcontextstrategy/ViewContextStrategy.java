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

package com.vaadin.cdi.viewcontextstrategy;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.vaadin.cdi.AfterViewChange;
import com.vaadin.cdi.NormalUIScoped;
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
 * <p>
 * Separate annotations annotated by {@link ViewContextStrategyQualifier}
 * have to exist for each of the implementations.
 * <p>
 * Example of a custom implementation:
 * <p>
 * A separate annotation.
 * <pre>
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * {@literal @}Target({ ElementType.TYPE })
 * {@literal @}ViewContextStrategyQualifier
 *  public {@literal @}interface MyStrategyAnnotation {
 *  }
 * </pre>
 * An implementation class.
 * <pre>
 * {@literal @}NormalUIScoped
 * {@literal @}MyStrategyAnnotation
 *  public class MyStrategy implements ViewContextStrategy {
 *    public boolean contains(String viewName, String parameters) {
 *      ...
 *    }
 *  }
 * </pre>
 * Use annotation on the view.
 * <pre>
 * {@literal @}CDIView("myView")
 * {@literal @}MyStrategyAnnotation
 *  public MyView implements View {
 *  ...
 *  }
 * </pre>
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

    @NormalUIScoped
    @ViewNameDriven
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

    @NormalUIScoped
    @ViewNameAndParametersDriven
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

    @NormalUIScoped
    @EveryNavigationDriven
    class Always implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
            return false;
        }
    }

}
