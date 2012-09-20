package com.vaadin.cdi.uis;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author  adam-bien.com
 */
public class InstrumentedView extends CustomComponent implements View{

    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();

    }
    @Override
    public void enter(ViewChangeEvent event) {
    }
  

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }


}
