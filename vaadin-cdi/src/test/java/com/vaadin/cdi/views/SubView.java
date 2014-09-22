package com.vaadin.cdi.views;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@CDIView(value=SubView.VIEW_ID)
public class SubView extends AbstractNavigatableView {
    public static final String VIEW_ID = MainView.VIEW_ID + "/" + "subview";
    private static final String DESCRIPTION_LABEL = "label";
    
    @Override
    protected Component buildContent() {
        Label label = new Label(VIEW_ID);
        label.setId(DESCRIPTION_LABEL);
        return label;
    }
    
}