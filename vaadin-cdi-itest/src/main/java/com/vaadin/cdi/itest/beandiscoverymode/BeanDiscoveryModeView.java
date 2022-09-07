package com.vaadin.cdi.itest.beandiscoverymode;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Route
@RouteScoped
@CdiComponent
public class BeanDiscoveryModeView extends Div {

    public static final String SET_NAME_BTN_ID = "SET_NAME_BTN_ID";
    public static final String CLEAR_NAME_BTN_ID = "CLEAR_NAME_BTN_ID";
    public static final String RESULT_SPAN_ID = "RESULT_SPAN_ID";
    public static final String NORMAL_SRV_BTN_ID = "NORMAL_SRV_BTN_ID";
    public static final String CDI_SRV_BTN_ID = "CDI_SRV_BTN_ID";

    @Inject
    private NormalScopedGreetService normalScopedGreetService;
    @Inject
    private CdiComponentGreetService cdiComponentGreetService;

    @PostConstruct
    public void init() {
        Input nameField = new Input();
        NativeButton setNameButton = new NativeButton("Set the Name in input",
                e -> nameField.setValue("MyName"));
        setNameButton.setId(SET_NAME_BTN_ID);
        NativeButton clearNameButton = new NativeButton("Clear the Name input",
                e -> nameField.setValue(""));
        clearNameButton.setId(CLEAR_NAME_BTN_ID);

        Span resultSpan = new Span();
        resultSpan.setId(RESULT_SPAN_ID);

        // Button click listeners can be defined as lambda expressions
        NativeButton normalServiceButton = new NativeButton("Normal Scoped Say hello",
                e -> resultSpan.setText(normalScopedGreetService.greet(nameField.getValue())));
        normalServiceButton.setId(NORMAL_SRV_BTN_ID);

        // Button click listeners can be defined as lambda expressions
        NativeButton cdiCompServiceButton = new NativeButton("CDI Component Say hello",
                e -> resultSpan.setText(cdiComponentGreetService.greet(nameField.getValue())));
        cdiCompServiceButton.setId(CDI_SRV_BTN_ID);

        add(nameField, resultSpan, normalServiceButton, cdiCompServiceButton);
    }
}
