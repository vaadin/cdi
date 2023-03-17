/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.itest.invaliddeployment;

import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.flow.component.html.Label;

@NormalUIScoped
public class NormalScopedLabel extends Label {
}
