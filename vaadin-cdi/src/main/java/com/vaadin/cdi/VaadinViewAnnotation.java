package com.vaadin.cdi;

import java.lang.annotation.Annotation;

class VaadinViewAnnotation implements VaadinView {
    private final String viewName;

    public VaadinViewAnnotation(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return VaadinView.class;
    }

    @Override
    public String value() {
        return viewName;
    }
}
