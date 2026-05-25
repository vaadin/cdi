/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

import jakarta.inject.Inject;

import com.vaadin.cdi.NormalViewScoped;
import com.vaadin.cdi.views.CrossInjectingView;

@NormalViewScoped
public class CrossInjectingBean {
    
    private CrossInjectingView parent;
    
    private CrossInjectingView constructorParent;
    
    public CrossInjectingBean() {
        constructorParent = null;
    }
    
    @Inject
    public CrossInjectingBean(CrossInjectingView view) {
        constructorParent = view;
    }
    
    @Inject
    public void setParentView(CrossInjectingView view) {
        this.parent = view;
    }
    
    public String getIdentifier() {
        return (parent != null) ? parent.getIdentifier() : "null"; 
    }
    
    public String getConstructorIdentifier() {
        return (constructorParent != null) ? constructorParent.getIdentifier() : "null"; 
    }
}

