package com.vaadin.terminal.gwt.server;

import com.vaadin.cdi.VaadinApplication;
import java.lang.annotation.Annotation;

/**
 *
 * @author adam-bien.com
 */
public class VaadinApplicationInstance implements VaadinApplication{

    public String mapping() {
        //non-binding so the return type does not really matter
        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return VaadinApplication.class;
    }
    
}