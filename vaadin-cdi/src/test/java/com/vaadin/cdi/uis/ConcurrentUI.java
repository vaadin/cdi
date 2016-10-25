package com.vaadin.cdi.uis;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.ConventionsAccess;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI
public class ConcurrentUI extends UI {

    public static final String LABEL = "label";
    public static final String COUNTER_BUTTON = "counter";
    public static final String COUNTER_LABEL = "counter-label";
    public static final String KILL_SESSION = "kill-session";
    public static final String OPEN_WINDOW = "open-window";

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private int clickCount;

    private int instanceClicks = 0;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
        clickCount = 0;

    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
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
                + ConventionsAccess.deriveMappingForUI(ConcurrentUI.class))
                .extend(windowOpenButton);

        layout.addComponent(label);
        layout.addComponent(counterLabel);
        layout.addComponent(clickButton);
        layout.addComponent(windowOpenButton);
        layout.addComponent(closeButton);
        setContent(layout);
    }

}
