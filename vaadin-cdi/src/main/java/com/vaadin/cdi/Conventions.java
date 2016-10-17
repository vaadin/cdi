package com.vaadin.cdi;

import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

/**
 * Instances of this class define what conventions should be used in the CDI enabled application.
 * This includes decisions on what classes constitute valid UIs and Views, and which View is valid for which UI.
 * <p>
 * It also includes decisions on how to construct the URI fragment for a given class.
 * <p>
 * If this interface is implemented by the application, it must be declared an @Alternative
 * and activated in the beans.xml
 */
public interface Conventions {

	/**
	 * Derive the URI fragment appropriate for this UI class.
	 *
	 * @param beanClass
	 * @return
	 */
	String deriveMappingForUI(Class<? extends UI> uiClass);

	/**
	 * Derive the URI fragment appropriate for this View.
	 *
	 * @param beanClass
	 * @return
	 */
	String deriveMappingForView(Class<? extends View> viewClass);

	/**
	 * Evaluates if the given class is a valid UI for this deployment.
	 * Classes for which true is returned may be used as parameters in subsequent calls to deriveMappingForUI
	 *
	 * @param beanClass
	 * @return
	 */
	boolean uiClassIsValid(Class<? extends UI> uiClass);

	/**
	 * Evaluates if the given class is a valid View for this deployment.
	 * Classes for which true is returned may be used as parameters in subsequent calls to deriveMappingForView
	 *
	 * @param viewClass
	 * @return
	 */
	boolean viewClassIsValid(Class<? extends View> viewClass);

	/**
	 * Evaluates if a given View class is valid for a particular UI. Navigation is only permitted if this method returns
	 * true.
	 *
	 * @param viewClass
	 * @param ui
	 * @return
	 */
	boolean viewClassIsValid(Class<? extends View> viewClass, UI ui);

	/**
	 * Evaluates whether this UI class is treated as the root UI or not. Only one UI class may return true from this method.
	 * If a class returns true from this method, it must return an empty String from deriveMappingForUI
	 *
	 * @param uiClass
	 * @return
	 */
	boolean uiClassIsRoot(Class<? extends UI> uiClass);
}
