package com.vaadin.cdi.example.logging;

import com.vaadin.cdi.UIScoped;

/**
 * Event that can be sent between different views of a UI.
 */
@UIScoped
public class LoggableEvent {
    private final String value;

    public LoggableEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
