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

package com.vaadin.cdi.internal;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIView;

public class Conventions {

     public static String deriveMappingForUI(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(CDIUI.class)) {
            CDIUI annotation = beanClass.getAnnotation(CDIUI.class);
            String mapping = annotation.value();
            if (mapping != null && !CDIUI.USE_CONVENTIONS.equals(mapping)) {
                return mapping;
            } else {
                // derive mapping from classname
                mapping = beanClass.getSimpleName().replaceFirst("UI$", "");
                return upperCamelToLowerHyphen(mapping);
            }
        } else {
            return null;
        }
    }

    public static String deriveMappingForView(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(CDIView.class)) {
            CDIView annotation = beanClass.getAnnotation(CDIView.class);
            if (annotation != null
                    && !CDIView.USE_CONVENTIONS.equals(annotation.value())) {
                return annotation.value();
            } else {
                String mapping = beanClass.getSimpleName().replaceFirst(
                        "View$", "");
                return upperCamelToLowerHyphen(mapping);
            }
        } else {
            return null;
        }
    }
    
    public static String upperCamelToLowerHyphen(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
                if (shouldPrependHyphen(string, i)) {
                    sb.append('-');
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean shouldPrependHyphen(String string, int i) {
        if (i == 0) {
            // Never put a hyphen at the beginning
            return false;
        } else if (!Character.isUpperCase(string.charAt(i - 1))) {
            // Append if previous char wasn't upper case
            return true;
        } else if (i + 1 < string.length()
                && !Character.isUpperCase(string.charAt(i + 1))) {
            // Append if next char isn't upper case
            return true;
        } else {
            return false;
        }
    }
}
