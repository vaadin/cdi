/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.cdi.uis;

import com.vaadin.cdi.internal.Counter;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 */
public class InstrumentedInterceptor {

    public static final String INTERCEPT_COUNT = "InstrumentedInterceptor";
    @Inject
    Counter counter;

    @AroundInvoke
    public Object intercept(InvocationContext invocationContext)
            throws Exception {
        System.out.println("---invoked: " + invocationContext.getMethod());
        counter.increment(INTERCEPT_COUNT);
        return invocationContext.proceed();

    }
}
