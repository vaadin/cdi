package com.vaadin.cdi.views;

import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.CrossInjectingBean;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView("")
public class CrossInjectingView extends CustomComponent implements View {

    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private final long id = NEXT_ID.incrementAndGet();
    
    @Inject private CrossInjectingBean bean;
    
    public static final String TRUE_ID = "true-id";
    public static final String SETTER_INJECTED_ID = "injected-id";
    public static final String CONSTRUCTOR_INJECTED_ID = "constructor-injected-id";
    
    
    @Override
    public void enter(ViewChangeEvent event) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        Label label = new Label("CrossInjectingView");
        label.setId("view");

        Label myId = new Label(getIdentifier());
        myId.setId(TRUE_ID);

        Label setterInjectedId = new Label(bean.getIdentifier());
        setterInjectedId.setId(SETTER_INJECTED_ID);

        Label constructorInjectedId = new Label(bean.getConstructorIdentifier());
        constructorInjectedId.setId(CONSTRUCTOR_INJECTED_ID);
        
        layout.addComponent(label);
        layout.addComponent(myId);
        layout.addComponent(setterInjectedId);
        layout.addComponent(constructorInjectedId);
        
        setCompositionRoot(layout);
    }

    public String getIdentifier() {
        return "CrossInjectingView/"+id;
    }
}
