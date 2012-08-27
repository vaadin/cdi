package com.vaadin.cdi;

import java.lang.annotation.Annotation;

import com.vaadin.Application;

class VaadinUIAnnotation implements VaadinUI {

	private final String mapping;
	private Class<? extends Application> application;

	public VaadinUIAnnotation(Class<? extends Application> application,
			String mapping) {
		this.application = application;
		this.mapping = mapping;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return VaadinUI.class;
	}

	@Override
	public String mapping() {
		return mapping;
	}

	@Override
	public Class<? extends Application> application() {
		return application;
	}

	public void setApplication(Class<? extends Application> application) {
		this.application = application;
	}
}
