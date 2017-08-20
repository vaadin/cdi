package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDINavigator;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;

@CDIUI("viewDestroy")
public class DestroyViewUI extends UI {
    public static final String CLOSE_BTN_ID = "close";
    public static final String LABEL_ID = "label";
    public static final String NAVIGATE_VIEW_BTN_ID = "navigateview";
    public static final String UIID_ID = "UIID";
    public static final String VIEWSCOPED_VIEW = "viewscoped";
    public static final String OTHER_VIEW = "other";
    public static final String NAVIGATE_ERROR_BTN_ID = "error";
    public static final String NAVIGATE_OTHER_BTN_ID = "other";

    @Inject
    CDINavigator navigator;
    @Inject
    Counter counter;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        final Label uiId = new Label(String.valueOf(getUIId()));
        uiId.setId(UIID_ID);
        layout.addComponent(uiId);

        Button closeBtn = new Button("close UI");
        closeBtn.setId(CLOSE_BTN_ID);
        closeBtn.addClickListener(event -> close());
        layout.addComponent(closeBtn);

        navigator.init(this, view -> {
        });
        navigator.setErrorView(ErrorView.class);

        Button otherNavigateBtn = new Button("navigate other");
        otherNavigateBtn.setId(NAVIGATE_OTHER_BTN_ID);
        otherNavigateBtn.addClickListener(event -> navigator.navigateTo(OTHER_VIEW));
        layout.addComponent(otherNavigateBtn);

        Button viewNavigateBtn = new Button("navigate view");
        viewNavigateBtn.setId(NAVIGATE_VIEW_BTN_ID);
        viewNavigateBtn.addClickListener(event -> navigator.navigateTo(VIEWSCOPED_VIEW));
        layout.addComponent(viewNavigateBtn);

        Button errorNavigateBtn = new Button("navigate error");
        errorNavigateBtn.setId(NAVIGATE_ERROR_BTN_ID);
        errorNavigateBtn.addClickListener(event -> navigator.navigateTo("nonexsistentview"));
        layout.addComponent(errorNavigateBtn);

        setContent(layout);
    }

    @CDIView(value = VIEWSCOPED_VIEW)
    public static class ViewScopedView implements View {
        public static final String DESTROY_COUNT = "viewdestroy";

        @Inject
        ViewScopedBean viewScopedBean;

        @Inject
        Counter counter;

        int uiId;

        @PreDestroy
        public void destroy() {
            counter.increment(DESTROY_COUNT + uiId);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
            uiId = UI.getCurrent().getUIId();
        }
    }

    @CDIView(value = OTHER_VIEW)
    public static class OtherView implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        }
    }

    @ViewScoped
    public static class ViewScopedBean implements Serializable {
        public static final String DESTROY_COUNT = "viewbeandestroy";

        @Inject
        Counter counter;

        int uiId;

        @PreDestroy
        public void destroy() {
            counter.increment(DESTROY_COUNT + uiId);
        }

        @PostConstruct
        public void contruct() {
            uiId = UI.getCurrent().getUIId();
        }

    }


    public static class ErrorView implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {

        }
    }
}
