package com.vaadin.cdi;

import java.lang.annotation.Annotation;

class VaadinViewAnnotation implements VaadinView {
    @Override
    public Class<? extends Annotation> annotationType() {
        return VaadinView.class;
    }

    @Override
    public String value() {
        // value does not matter
        return null;
    }
}
