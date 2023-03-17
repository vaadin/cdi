/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.service;

import com.vaadin.cdi.annotation.VaadinServiceEnabled;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.SystemMessagesProvider;

@VaadinServiceEnabled
@VaadinServiceScoped
public class TestSystemMessagesProvider implements SystemMessagesProvider {

    public static final String EXPIRED_BY_TEST = "EXPIRED BY TEST";

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
        CustomizedSystemMessages messages = new CustomizedSystemMessages();
        messages.setSessionExpiredNotificationEnabled(true);
        messages.setSessionExpiredMessage(EXPIRED_BY_TEST);
        // On Wildfy this message pops up after session invalidate button.
        messages.setCookiesDisabledMessage(EXPIRED_BY_TEST);
        return messages;
    }
}
