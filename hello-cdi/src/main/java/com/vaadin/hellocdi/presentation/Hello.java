package com.vaadin.hellocdi.presentation;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinUIScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

/**
 * 
 * @author adam-bien.com
 */
@VaadinUI(mapping = "helloCDI")
public class Hello extends UI {
    @Override
    protected void init(WrappedRequest request) {
        Navigator.SimpleViewDisplay viewDisplay = new Navigator.SimpleViewDisplay();
        viewDisplay.setCaption("Hello CDI");
        setContent(viewDisplay);
    }

}
