package com.vaadin.reports.presentation;

import java.util.Date;

import javax.inject.Inject;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.reports.business.issues.boundary.BugTracking;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

@VaadinUI(mapping = "reports")
public class ReportsRoot extends UI {

    @Inject
    BugTracking bt;

    @Override
    protected void init(WrappedRequest request) {
        addComponent(new Button("New Bug", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                bt.fileBug("New bug: " + new Date());
            }
        }));
    }
}
