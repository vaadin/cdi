/*
 * Copyright 2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi.integrationtests.uis;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@VaadinUI
public class PlainUI extends UI {

    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    @PreDestroy
    public void destroy() {
        COUNTER.decrementAndGet();
    }

    @Override
    protected void init(VaadinRequest request) {
        final Label label = new Label();
        label.setId("label");
        label.setValue("Hello " + getUIId());
        setContent(new VerticalLayout(label));
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    public static void resetCounter() {
        COUNTER.set(0);
    }
}
