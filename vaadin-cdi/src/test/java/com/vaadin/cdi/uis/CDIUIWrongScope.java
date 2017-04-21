package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@CDIUI
@NormalUIScoped
public class CDIUIWrongScope extends UI {
    @Override
    protected void init(VaadinRequest request) {

    }
}
