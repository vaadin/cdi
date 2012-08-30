package com.vaadin.cdi;

import java.lang.annotation.Annotation;

class VaadinUIAnnotation implements VaadinUI {

    private final String mapping;

    public VaadinUIAnnotation(String mapping) {
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
}
