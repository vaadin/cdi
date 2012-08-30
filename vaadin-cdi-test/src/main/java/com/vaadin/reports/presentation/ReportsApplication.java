package com.vaadin.reports.presentation;

import com.vaadin.Application;
import com.vaadin.cdi.VaadinApplication;

@VaadinApplication(mapping = "/reports/*")
public class ReportsApplication extends Application {

    @Override
    public void init() {
        super.init();
    }
}
