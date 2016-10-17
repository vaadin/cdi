package com.vaadin.cdi.conventions;

import javax.enterprise.inject.Alternative;

import com.vaadin.cdi.Conventions;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

@Alternative
public class AlternateConventions implements Conventions {

	@Override
	public String deriveMappingForUI(Class<? extends UI> beanClass) {
		return beanClass.getSimpleName();
	}

	@Override
	public String deriveMappingForView(Class<? extends View> beanClass) {
		return beanClass.getSimpleName();
	}

	@Override
	public boolean uiClassIsValid(Class<? extends UI> beanClass) {
		return true;
	}

	@Override
	public boolean viewClassIsValid(Class<? extends View> beanClass) {
		return true;
	}

	@Override
	public boolean viewClassIsValid(Class<? extends View> viewClass, UI ui) {
		return true;
	}

	@Override
	public boolean uiClassIsRoot(Class<? extends UI> uiClass) {
		return true;
	}
}
