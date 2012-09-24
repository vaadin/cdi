package com.vaadin.cdi;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;

/**
 *
 * @author adam-bien.com
 */
@VaadinUI
public class VaadinScopedBean {

    public static AtomicInteger COUNTER = new AtomicInteger(0);
    
    @PostConstruct
    public void initialized(){
        COUNTER.incrementAndGet();
    }
    
    
}
