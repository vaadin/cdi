package com.vaadin.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;

import javax.enterprise.context.Dependent;

@CDIView("viewDependent")
@Dependent
public class CDIViewDependent implements View {

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
