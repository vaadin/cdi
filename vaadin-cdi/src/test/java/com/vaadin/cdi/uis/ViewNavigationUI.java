/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.uis;

import com.vaadin.cdi.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@CDIUI("")
public class ViewNavigationUI extends UI {
    public static final String REVERTED_NAV_BTN_ID = "revertednavbtn";
    public static final String SUCCESS_NAV_BTN_ID = "successnavbtn";
    public static final String VALUE_LABEL_ID = "valuelabel";
    public static final String DEFAULTVIEW_VALUE = "defaultview";
    private static final String LABEL_ID = "label";
    private static final String REVERTME = "revertme";
    private static final String SUCCESS = "success";
    public static final String SUCCESSVIEW_VALUE = "successview";
    public static final String BEFORE_VALUE_LABEL_ID = "beforevaluelabel";
    public static final String AFTER_VALUE_LABEL_ID = "aftervaluelabel";
    public static final String CDIAFTER_VALUE_LABEL_ID = "cdiaftervaluelabel";
    public static final String SHOW_VIEW_VALUE_LABEL_ID = "viewcomponentvaluelabel";

    @Inject
    CDINavigator navigator;
    @Inject
    ViewScopedBean bean;
    private Label showViewValue;
    private Label value;
    private Label cdiAfterValue;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        value = new Label();
        value.setId(VALUE_LABEL_ID);
        layout.addComponent(value);

        final Label beforeValue = new Label();
        beforeValue.setId(BEFORE_VALUE_LABEL_ID);
        layout.addComponent(beforeValue);

        showViewValue = new Label();
        showViewValue.setId(SHOW_VIEW_VALUE_LABEL_ID);
        layout.addComponent(showViewValue);

        final Label afterValue = new Label();
        afterValue.setId(AFTER_VALUE_LABEL_ID);
        layout.addComponent(afterValue);

        cdiAfterValue = new Label();
        cdiAfterValue.setId(CDIAFTER_VALUE_LABEL_ID);
        layout.addComponent(cdiAfterValue);

        final Panel viewDisplayPanel = new Panel();
        viewDisplayPanel.setContent(new Label());
        layout.addComponent(viewDisplayPanel);

        navigator.init(this, view -> {
            showViewValue.setValue(bean.getValue());
            if (view instanceof Component) {
                viewDisplayPanel.setContent((Component) view);
            }
        });

        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                if (event.getViewName().equals(REVERTME)) {
                    return false;
                } else {
                    if (event.getOldView() != null) {
                        beforeValue.setValue(bean.getValue());
                    } else {
                        // given no oldView, we have no view context during beforeViewChange
                        try {
                            bean.getValue();
                        } catch (ContextNotActiveException e) {
                            beforeValue.setValue(e.getClass().getSimpleName());
                        }
                    }
                    return true;
                }
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                afterValue.setValue(bean.getValue());
            }
        });

        createNavBtn(layout, REVERTED_NAV_BTN_ID, REVERTME);
        createNavBtn(layout, SUCCESS_NAV_BTN_ID, SUCCESS);

        setContent(layout);
    }

    private void createNavBtn(VerticalLayout layout, String navBtnId, String targetView) {
        Button navigateBtn = new Button(navBtnId);
        navigateBtn.setId(navBtnId);
        navigateBtn.addClickListener(event -> {
            navigator.navigateTo(targetView);
            value.setValue(bean.getValue());
        });
        layout.addComponent(navigateBtn);
    }

    @CDIView("")
    public static class DefaultView implements View {
        @Inject
        ViewScopedBean bean;

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.setValue(DEFAULTVIEW_VALUE);
        }
    }

    @CDIView(REVERTME)
    public static class RevertMeView implements View {
        @Inject
        ViewScopedBean bean;


        @PostConstruct
        private void init() {
            bean.setValue("revertedview");
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            throw new IllegalStateException("Should never happen");
        }
    }

    @CDIView(SUCCESS)
    public static class SuccessView implements View {
        @Inject
        ViewScopedBean bean;

        @PostConstruct
        private void init() {
            bean.setValue(SUCCESSVIEW_VALUE);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

    @NormalViewScoped
    public static class ViewScopedBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private void onAfterViewChange(@Observes @AfterViewChange ViewChangeListener.ViewChangeEvent event) {
        cdiAfterValue.setValue(bean.getValue());
    }

}
