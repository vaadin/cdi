package com.vaadin.cdi.uis;

import com.vaadin.cdi.*;
import com.vaadin.cdi.internal.Counter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@CDIUI("")
public class ViewNavigationUI extends UI {
    public static final String REVERTED_NAV_BTN_ID = "revertednavbtn";
    public static final String SUCCESS_NAV_BTN_ID = "successnavbtn";
    public static final String DELAY_NAV_BTN_ID = "delayednavbtn";
    public static final String VALUE_LABEL_ID = "valuelabel";
    public static final String DEFAULTVIEW_VALUE = "defaultview";
    private static final String LABEL_ID = "label";
    private static final String REVERTME = "revertme";
    private static final String SUCCESS = "success";
    private static final String DELAY = "delay";
    public static final String CHANGEDSUCCESS_VALUE = "successother";
    public static final String SUCCESSVIEW_VALUE = "successview";
    public static final String DELAYVIEW_VALUE = "delayview";
    public static final String CHANGE_VALUE_BTN_ID = "othervalue";
    public static final String BEFORE_VALUE_LABEL_ID = "beforevaluelabel";
    public static final String AFTER_VALUE_LABEL_ID = "aftervaluelabel";
    public static final String CDIAFTER_VALUE_LABEL_ID = "cdiaftervaluelabel";
    public static final String BEFORE_LEAVE_VALUE_LABEL_ID = "beforeleavevaluelabel";
    public static final String SHOW_VIEW_VALUE_LABEL_ID = "viewcomponentvaluelabel";

    @Inject
    CDINavigator navigator;
    @Inject
    ViewScopedBean bean;
    private Label beforeLeaveValue;
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

        beforeLeaveValue = new Label();
        beforeLeaveValue.setId(BEFORE_LEAVE_VALUE_LABEL_ID);
        layout.addComponent(beforeLeaveValue);

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
        createNavBtn(layout, DELAY_NAV_BTN_ID, DELAY);

        Button changeValueBtn = new Button("changevalue");
        changeValueBtn.setId(CHANGE_VALUE_BTN_ID);
        changeValueBtn.addClickListener(event -> {
            bean.setValue(CHANGEDSUCCESS_VALUE);
            value.setValue(bean.getValue());
        });
        layout.addComponent(changeValueBtn);

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

        @Override
        public void beforeLeave(ViewBeforeLeaveEvent event) {
            ((ViewNavigationUI) UI.getCurrent()).beforeLeaveValue.setValue(bean.getValue());
            event.navigate();
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
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "successconstructcount";

        @PostConstruct
        private void init() {
            bean.setValue(SUCCESSVIEW_VALUE);
            counter.increment(CONSTRUCT_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

    @CDIView(DELAY)
    public static class DelayNavigationView extends VerticalLayout implements View {
        @Inject
        ViewScopedBean bean;
        Button performDelayedNavBtn;
        public static final String PREFORM_DELAYED_NAV_BTN_ID = "performDelayedNavBtn";

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.setValue(DELAYVIEW_VALUE);
            performDelayedNavBtn = new Button(PREFORM_DELAYED_NAV_BTN_ID);
            performDelayedNavBtn.setId(PREFORM_DELAYED_NAV_BTN_ID);
            addComponent(performDelayedNavBtn);
        }

        @Override
        public void beforeLeave(ViewBeforeLeaveEvent event) {
            performDelayedNavBtn.addClickListener(clickEvent -> {
                event.navigate();
                ((ViewNavigationUI) UI.getCurrent()).value.setValue(bean.getValue());
            });
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
