/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
