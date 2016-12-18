package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDINavigator;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import javax.inject.Inject;

@CDIUI("")
public class NonPassivatingUI extends UI {

    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this,this);
    }

}
