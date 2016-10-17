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
 */

package com.vaadin.cdi.internal;

import com.vaadin.cdi.Conventions;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * Static access point for currently active Conventions in the application.
 */
public class ConventionsAccess {

	private static Conventions getConventions() {
		Conventions contextualReference = BeanProvider.getContextualReference(Conventions.class, true);
		if(contextualReference != null) {
			return contextualReference;
		} else {
			return new DefaultConventions();
		}
	}

	public static String deriveMappingForUI(Class<? extends UI> uiClass) {
		return getConventions().deriveMappingForUI(uiClass);
	}

	public static String deriveMappingForView(Class<? extends View> viewClass) {
		return getConventions().deriveMappingForView(viewClass);
	}

	public static boolean uiClassIsValid(Class<? extends UI> uiClass) {
		return getConventions().uiClassIsValid(uiClass);
	}

	public static boolean viewClassIsValid(Class<? extends View> viewClass) {
		return getConventions().viewClassIsValid(viewClass);
	}

	public static boolean viewClassIsValid(Class<? extends View> viewClass, UI ui) {
		Conventions conventions = getConventions();
		return conventions.viewClassIsValid(viewClass) && conventions.viewClassIsValid(viewClass, ui);
	}

	public static boolean uiClassIsRoot(Class<? extends UI> uiClass) {
		return getConventions().uiClassIsRoot(uiClass);
	}
}
