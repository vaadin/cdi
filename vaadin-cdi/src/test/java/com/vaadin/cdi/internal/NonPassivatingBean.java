package com.vaadin.cdi.internal;

import com.vaadin.cdi.ViewScoped;

// Does not implement Serializable
@ViewScoped
public class NonPassivatingBean {

    private String someString = "NonPassivatingBean" + hashCode();
    
    public String getSomeString() {
        return someString;
    }
}
