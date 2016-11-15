package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.Serializable;

@CDIUI("viewDestroy")
public class DestroyViewUI extends UI {
    public static final String CLOSE_BTN_ID = "close";
    public static final String LABEL_ID = "label";
    public static final String NAVIGATE_DEPENDENT_BTN_ID = "navigatedep";
    public static final String NAVIGATE_VIEW_BTN_ID = "navigateview";
    public static final String UIID_ID = "UIID";
    public static final String DEPENDENT_VIEW = "dependent";
    public static final String VIEWSCOPED_VIEW = "viewscoped";

    @Inject
    CDIViewProvider viewProvider;
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
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        layout.addComponent(closeBtn);

        final Navigator navigator = new Navigator(this, new ViewDisplay() {
            @Override
            public void showView(View view) {
            }
        });
        navigator.addProvider(viewProvider);

        Button viewNavigateBtn = new Button("navigate dependent");
        viewNavigateBtn.setId(NAVIGATE_DEPENDENT_BTN_ID);
        viewNavigateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(DEPENDENT_VIEW);
            }
        });
        layout.addComponent(viewNavigateBtn);

        Button dependentNavigateBtn = new Button("navigate view");
        dependentNavigateBtn.setId(NAVIGATE_VIEW_BTN_ID);
        dependentNavigateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(VIEWSCOPED_VIEW);
            }
        });
        layout.addComponent(dependentNavigateBtn);

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

    @CDIView(DEPENDENT_VIEW)
    @Dependent
    public static class DependentView implements View {
        public static final String DESTROY_COUNT = "DependentViewDestroy";

        @Inject
        Counter counter;

        int uiId;

        @PreDestroy
        public void destroy() {
            counter.increment(DESTROY_COUNT + uiId);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            uiId = UI.getCurrent().getUIId();
        }
    }


}
