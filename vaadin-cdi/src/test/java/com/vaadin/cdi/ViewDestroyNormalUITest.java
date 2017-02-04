package com.vaadin.cdi;

import com.vaadin.cdi.uis.DestroyViewNormalUI;
import com.vaadin.cdi.uis.DestroyViewUI;

public class ViewDestroyNormalUITest extends ViewDestroyTest {
    @Override
    protected Class<? extends DestroyViewUI> getUIClass() {
        return DestroyViewNormalUI.class;
    }
}
