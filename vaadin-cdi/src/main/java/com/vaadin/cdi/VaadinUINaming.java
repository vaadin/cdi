package com.vaadin.cdi;

public class VaadinUINaming {

    static String firstToLower(String name) {
        char firstLower = Character.toLowerCase(name.charAt(0));
        if (name.length() > 1) {
            return firstLower + name.substring(1);
        } else {
            return String.valueOf(firstLower);
        }
    }

    public static String deriveNameFromConvention(Class<?> clazz){
        return firstToLower(clazz.getSimpleName());
    }
}
