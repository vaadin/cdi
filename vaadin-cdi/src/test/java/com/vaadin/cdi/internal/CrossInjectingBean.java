package com.vaadin.cdi.internal;

import javax.inject.Inject;

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

