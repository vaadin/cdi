package com.vaadin.cdi.viewscope;

import com.vaadin.navigator.View;
import com.vaadin.util.CurrentInstance;

/**
 * Associates a view with the current thread
 * 
 * @author adam-bien.com
 */
public class CurrentView {

    public static void set(View current) {
        CurrentInstance.setInheritable(View.class, current);
    }

    public static View getCurrent() {
        return CurrentInstance.get(View.class);
    }
}
