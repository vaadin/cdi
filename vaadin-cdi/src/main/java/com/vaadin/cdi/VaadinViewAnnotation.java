package com.vaadin.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.util.Nonbinding;

import com.vaadin.ui.UI;

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

    @Override
    @Nonbinding
    public String[] rolesAllowed() {
        return null; // nonbinding
    }

    @Override
    public Class<? extends UI> ui() {
        return null; // nonbinding
    }
}
