/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.cdi.itest.service;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.itest.Counter;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.UIInitEvent;

@CdiComponent
public class EventObserver {
    @Inject
    private Counter counter;

    private void onSessionInit(@Observes SessionInitEvent sessionInitEvent) {
        counter.increment(SessionInitEvent.class.getSimpleName());
    }

    private void onSessionDestroy(@Observes SessionDestroyEvent sessionDestroyEvent) {
        counter.increment(SessionDestroyEvent.class.getSimpleName());
    }

    private void onUIInit(@Observes UIInitEvent uiInitEvent) {
        counter.increment(UIInitEvent.class.getSimpleName());
    }
}
