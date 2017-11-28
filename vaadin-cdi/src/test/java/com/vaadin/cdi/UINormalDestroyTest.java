package com.vaadin.cdi;

import com.vaadin.cdi.uis.DestroyNormalUI;
import com.vaadin.cdi.uis.DestroyUI;

public class UINormalDestroyTest extends UIDestroyTest {
    @Override
    protected Class<? extends DestroyUI> getUIClass() {
        return DestroyNormalUI.class;
    }
}