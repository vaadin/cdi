/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.cdi.internal;

import com.vaadin.cdi.VaadinUI;
import com.vaadin.cdi.VaadinView;

public class Conventions {

    static String firstToLower(String name) {
        char firstLower = Character.toLowerCase(name.charAt(0));
        if (name.length() > 1) {
            return firstLower + name.substring(1);
        } else {
            return String.valueOf(firstLower);
        }
    }

    static String deriveNameFromConvention(Class<?> clazz) {
        return firstToLower(clazz.getSimpleName());
    }

    public static String deriveMappingForUI(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(VaadinUI.class)) {
            VaadinUI annotation = beanClass.getAnnotation(VaadinUI.class);
            String mapping = annotation.value();
            if (mapping != null && !mapping.isEmpty()) {
                return mapping;
            } else {
                return deriveNameFromConvention(beanClass);
            }
        } else {
            return null;
        }
    }

    public static String deriveMappingForView(Class<?> clazz) {
        VaadinView annotation = clazz.getAnnotation(VaadinView.class);
        if (annotation == null || annotation.value().isEmpty()) {
            return Conventions.deriveNameFromConvention(clazz);
        } else {
            return annotation.value();
        }
    }
}
