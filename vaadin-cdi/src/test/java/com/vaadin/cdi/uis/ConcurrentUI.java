package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIUI
public class ConcurrentUI extends UI {

    public static final String LABEL = "label";
    public static final String COUNTER_BUTTON = "counter";
    public static final String COUNTER_LABEL = "counter-label";
    public static final String KILL_SESSION = "kill-session";
    public static final String OPEN_WINDOW = "open-window";
    public static final String CONSTRUCT_COUNT = "ConcurrentUIConstruct";
    private int clickCount;

    @Inject
    Counter counter;

    @PostConstruct
    public void initialize() {
        counter.increment(CONSTRUCT_COUNT);
        clickCount = 0;

    }

    @Override
    protected void init(VaadinRequest request) {
        Layout layout = new VerticalLayout();
        final Label label = new Label("+ConcurrentUI");
        label.setId(LABEL);
        final Label counterLabel = new Label(String.valueOf(clickCount));
        counterLabel.setId(COUNTER_LABEL);
        Button clickButton = new Button("Increment");
        clickButton.setId(COUNTER_BUTTON);
        clickButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counterLabel.setValue(String.valueOf(++clickCount));
            }
        });

        Button closeButton = new Button("Close session");
        closeButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                VaadinSession.getCurrent().close();
            }
        });

        Button windowOpenButton = new Button("Open new window");
        windowOpenButton.setId(OPEN_WINDOW);
        // Note: cannot use new BWO(Class) because it would use
        // BrowserWindowOpenerUIProvider, not CDIUIProvider
        new BrowserWindowOpener(request.getContextPath() + "/"
                + Conventions.deriveMappingForUI(ConcurrentUI.class))
                .extend(windowOpenButton);

        layout.addComponent(label);
        layout.addComponent(counterLabel);
        layout.addComponent(clickButton);
        layout.addComponent(windowOpenButton);
        layout.addComponent(closeButton);
        setContent(layout);
    }

}
