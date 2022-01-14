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

package com.vaadin.cdi.itest.push;

import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.cdi.annotation.VaadinServiceScoped;
import com.vaadin.cdi.annotation.VaadinSessionScoped;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import org.apache.deltaspike.core.util.ContextUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import java.lang.annotation.Annotation;
import java.util.concurrent.locks.Lock;

public class PushComponent extends Div {

    public static final String RUN_BACKGROUND = "RUN_BACKGROUND";
    public static final String RUN_FOREGROUND = "RUN_FOREGROUND";

    private class ContextCheckTask implements Runnable {

        private final UI ui;
        private ClassLoader classLoader;

        private ContextCheckTask(UI ui) {
            this.ui = ui;
            this.classLoader = Thread.currentThread().getContextClassLoader();
        }

        @Override
        public void run() {
            // We can acquire the lock after the request started this thread is processed
            // Needed to make sure that this is sent as a push message
            Thread.currentThread().setContextClassLoader(this.classLoader);
            Lock lockInstance = ui.getSession().getLockInstance();
            lockInstance.lock();
            lockInstance.unlock();

            ui.access(PushComponent.this::print);
        }

    }

    @Resource
    private ManagedThreadFactory threadFactory;

    private void print() {
        printContextIsActive(RequestScoped.class);
        printContextIsActive(SessionScoped.class);
        printContextIsActive(ApplicationScoped.class);
        printContextIsActive(UIScoped.class);
        printContextIsActive(RouteScoped.class);
        printContextIsActive(VaadinServiceScoped.class);
        printContextIsActive(VaadinSessionScoped.class);
    }

    private void printContextIsActive(Class<? extends Annotation> scope) {
        Label label = new Label(ContextUtils.isContextActive(scope) + "");
        label.setId(scope.getName());
        add(new Div(new Label(scope.getSimpleName() + ": "), label));
    }

    @PostConstruct
    private void init() {
        NativeButton bgButton = new NativeButton("background", event -> {
            ContextCheckTask task = new ContextCheckTask(UI.getCurrent());
            Thread thread = threadFactory.newThread(task);
            thread.start();
        });
        bgButton.setId(RUN_BACKGROUND);

        NativeButton fgButton = new NativeButton("foreground", event
                -> print());
        fgButton.setId(RUN_FOREGROUND);

        add(bgButton, fgButton);
    }

}
