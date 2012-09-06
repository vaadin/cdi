package com.vaadin.cdi;

import javax.enterprise.event.Observes;

/**
 *
 * @author adam-bien.com
 */
@VaadinUIScoped
public class VaadinScopedEventListener {

    public static String RECEIVED;
    
    public void onScopedEvent(@Observes String payload){
        RECEIVED = payload;
    }
}
