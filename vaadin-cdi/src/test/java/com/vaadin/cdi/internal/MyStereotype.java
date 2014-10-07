package com.vaadin.cdi.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

import com.vaadin.cdi.NormalUIScoped;

@NormalUIScoped
@Stereotype
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyStereotype {

}
