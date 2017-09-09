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


import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

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
     * Strategy to release, and create a new context on every navigation
     * regardless of view name and parameters.
     */
    @RequestScoped
    class Always implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
            return false;
        }
    }

}
