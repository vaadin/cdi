package com.vaadin.cdi.internal;

import java.util.Arrays;
import java.util.List;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.Conventions;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

/**
 * The default implementation of Conventions.
 * <p>
 * The implementation relies on the presence of CDIUI and CDIView annotations,
 * and uses a camelcase-to-hyphenated convention with -View and -UI postfixes stripped.
 */
public class DefaultConventions implements Conventions {

	@Override
	public String deriveMappingForUI(Class<? extends UI> beanClass) {
		if(beanClass.isAnnotationPresent(CDIUI.class)) {
			CDIUI annotation = beanClass.getAnnotation(CDIUI.class);
			String mapping = annotation.value();
			if(mapping != null && !CDIUI.USE_CONVENTIONS.equals(mapping)) {
				return mapping;
			} else {
				// derive mapping from classname
				mapping = beanClass.getSimpleName().replaceFirst("UI$", "");
				return upperCamelToLowerHyphen(mapping);
			}
		} else {
			return null;
		}
	}

	@Override
	public String deriveMappingForView(Class<? extends View> beanClass) {
		if(beanClass.isAnnotationPresent(CDIView.class)) {
			CDIView annotation = beanClass.getAnnotation(CDIView.class);
			if(annotation != null
					&& !CDIView.USE_CONVENTIONS.equals(annotation.value())) {
				return annotation.value();
			} else {
				String mapping = beanClass.getSimpleName().replaceFirst(
						"View$", "");
				return upperCamelToLowerHyphen(mapping);
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean uiClassIsValid(Class<? extends UI> beanClass) {
		return beanClass.isAnnotationPresent(CDIUI.class);
	}

	@Override
	public boolean viewClassIsValid(Class<? extends View> beanClass) {
		return beanClass.isAnnotationPresent(CDIView.class);
	}

	@Override
	public boolean viewClassIsValid(Class<? extends View> viewClass, UI ui) {
		CDIView viewAnnotation = viewClass.getAnnotation(CDIView.class);
		List<Class<? extends UI>> uiClasses = Arrays.asList(viewAnnotation.uis());

		if(uiClasses.contains(UI.class)) {
			return true;
		} else {
			for(Class<? extends UI> uiClass : uiClasses) {
				if(uiClass.isAssignableFrom(ui.getClass())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean uiClassIsRoot(Class<? extends UI> uiClass) {
		if(!uiClass.isAnnotationPresent(CDIUI.class)) {
			return false;
		}
		return uiClass.getAnnotation(CDIUI.class).value().equals("");
	}

	public String upperCamelToLowerHyphen(String string) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(Character.isUpperCase(c)) {
				c = Character.toLowerCase(c);
				if(shouldPrependHyphen(string, i)) {
					sb.append('-');
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	private boolean shouldPrependHyphen(String string, int i) {
		if(i == 0) {
			// Never put a hyphen at the beginning
			return false;
		} else if(!Character.isUpperCase(string.charAt(i - 1))) {
			// Append if previous char wasn't upper case
			return true;
		} else // Append if next char isn't upper case
		{
			return i + 1 < string.length()
					&& !Character.isUpperCase(string.charAt(i + 1));
		}
	}
}
