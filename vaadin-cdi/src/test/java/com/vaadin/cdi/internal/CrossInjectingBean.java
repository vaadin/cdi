package com.vaadin.cdi.internal;

import javax.inject.Inject;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.views.CrossInjectingView;
import com.vaadin.navigator.View;

public class CrossInjectingBean {
    
    private CrossInjectingView parent;
    
    private CrossInjectingView constructorParent;
    
    @Inject
    public CrossInjectingBean(CrossInjectingView view) {
        constructorParent = view;
    }
    
    @Inject
    @ViewScoped
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

