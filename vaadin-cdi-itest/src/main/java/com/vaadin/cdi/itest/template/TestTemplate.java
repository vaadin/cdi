/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.template;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;

@Tag("test-template")
@JsModule("./test-template.js")
@UIScoped
@Route("")
public class TestTemplate extends PolymerTemplate<TemplateModel> {
    private @Id("input") Input input;

    private @Id("label") Label label;

    private @Inject Event<InputChangeEvent> setTextEventTrigger;

    public TestTemplate() {
        input.addValueChangeListener(event -> {
            setTextEventTrigger.fire(new InputChangeEvent(input.getValue()));
        });
    }

    private void onSetText(@Observes InputChangeEvent event) {
        label.setText(event.getText());
    }

    public static class InputChangeEvent {
        private final String text;

        public InputChangeEvent(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
