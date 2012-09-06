/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaadin.cdi;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;

/**
 *
 * @author adam-bien.com
 */
@VaadinUIScoped
public class VaadinScopedBean {

    public static AtomicInteger COUNTER = new AtomicInteger(0);
    
    @PostConstruct
    public void initialized(){
        COUNTER.incrementAndGet();
    }
    
    
}
