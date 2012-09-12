package com.vaadin.cdi.views;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinScopedBean;
import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

/**
 * 
 * @author adam-bien.com
 */
@VaadinUI
@VaadinUIScoped
public class MainUI extends UI {

    public static AtomicInteger COUNTER = new AtomicInteger(0);

    @Inject
    VaadinScopedBean first;

    @Inject
    VaadinScopedBean second;

    public MainUI() {
        COUNTER.incrementAndGet();
    }

    @Override
    protected void init(WrappedRequest request) {
        System.out.println("Initialized: " + request);
    }

}
