package com.vaadin.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.util.Nonbinding;

class VaadinUIAnnotation implements VaadinUI {

    @Override
    public Class<? extends Annotation> annotationType() {
        return VaadinUI.class;
    }

    @Override
    @Nonbinding
    public String mapping() {
        return null;
    }
}
