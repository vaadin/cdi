package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.Root;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author: adam-bien.com
 */
@Root
public class RootUI extends UI {
    private final static AtomicInteger COUNTER = new AtomicInteger(0);
    private int clickCount;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
        this.clickCount = 0;

    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("+RootUI");
        label.setId("label");
        layout.addComponent(label);
        setContent(layout);
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }
}
