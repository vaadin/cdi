package com.vaadin.cdi.internal;

import javax.enterprise.inject.Typed;
import java.util.concurrent.atomic.AtomicLong;

@Typed()
public class ProducedBean {
    private static final AtomicLong counter = new AtomicLong(0);
    private final long id = counter.incrementAndGet();
    private final String key;
    
    public ProducedBean() {
        key = "direct inject";
    }
    
    public ProducedBean(String key) {
        this.key = key;
    }
    
    public String getId() {
        return key+"/"+id;
    }
}
