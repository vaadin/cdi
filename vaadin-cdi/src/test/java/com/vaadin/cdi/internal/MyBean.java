package com.vaadin.cdi.internal;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.UI;

@UIScoped
public class MyBean {
    private static int counter = 0;

    private final int id = counter++;

    public MyBean() {
        System.out.println(UI.getCurrent());
        System.out.println("Created MyBean with id " + id);
    }

    public int getId() {
        return id;
    }
}
