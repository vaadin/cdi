/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.service;

import javax.enterprise.event.Observes;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.communication.IndexHtmlResponse;

public class BootstrapCustomizer {

    public static final String APPENDED_ID = "TEST_ID";
    public static final String APPENDED_TXT = "By Test";

    private void onServiceInit(@Observes ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.addIndexHtmlRequestListener(this::modifyBootstrapPage);
    }

    private void modifyBootstrapPage(IndexHtmlResponse response) {
        response.getDocument().body()
                .append("<p id='" + APPENDED_ID + "'>" + APPENDED_TXT + "</p>");
    }
}
