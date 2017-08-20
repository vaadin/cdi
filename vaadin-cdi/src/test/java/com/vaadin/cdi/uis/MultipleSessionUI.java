package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.internal.MyBean;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.CurrentInstance;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@CDIUI("")
public class MultipleSessionUI extends UI {

    private Layout layout = new VerticalLayout();

    public static final String MAINSESSION_ID = "mainsession";
    public static final String MAINSESSION2_ID = "mainsession2";
    public static final String OTHERSESSION_ID = "othersession";

    @Inject
    private MyBean bean;

    @PostConstruct
    private void test() {
        Label mainSessionLabel = new Label("" + bean.getBeanId());
        mainSessionLabel.setId(MAINSESSION_ID);
        layout.addComponent(mainSessionLabel);

        // set another session as the current session to simulate a second user
        VaadinSession otherSession = new VaadinSession(
                VaadinService.getCurrent()) {
            private ReentrantLock lock = new ReentrantLock();

            @Override
            public Lock getLockInstance() {
                return lock;
            }
        };
        Map<Class<?>, CurrentInstance> oldCurrentInstance = CurrentInstance
                .setCurrent(otherSession);
        otherSession.getLockInstance().lock();
        UI.setCurrent(this);
        // proxy looks up actual bean based on current session and UI
        Label otherSessionLabel = new Label("" + bean.getBeanId());
        otherSessionLabel.setId(OTHERSESSION_ID);
        layout.addComponent(otherSessionLabel);
        otherSession.getLockInstance().unlock();
        CurrentInstance.restoreInstances(oldCurrentInstance);

        Label mainSessionLabel2 = new Label("" + bean.getBeanId());
        mainSessionLabel2.setId(MAINSESSION2_ID);
        layout.addComponent(mainSessionLabel2);
    }

    @Override
    protected void init(VaadinRequest request) {
        System.out.println("init(): " + bean.getBeanId());

        Label label = new Label("+MultipleSessionUI");
        label.setId("label");
        layout.addComponent(label);

        setContent(layout);
    }
}
