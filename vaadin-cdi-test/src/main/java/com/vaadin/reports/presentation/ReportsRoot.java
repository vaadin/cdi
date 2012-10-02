package com.vaadin.reports.presentation;

import java.util.Date;

import javax.inject.Inject;

import com.vaadin.cdi.Mapping;
import com.vaadin.reports.business.issues.boundary.BugTracking;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

@Mapping(value = "reports")
public class ReportsRoot extends UI {

    @Inject
    BugTracking bt;

    @Override
    protected void init(VaadinRequest request) {
        addComponent(new Button("New Bug", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                bt.fileBug("New bug: " + new Date());
            }
        }));
    }
}
