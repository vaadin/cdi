package com.vaadin.cdi;

import javax.servlet.http.HttpServletRequest;


/**
 * @author: adam-bien.com
 */
public class Request {

    private static InheritableThreadLocal<HttpServletRequest> current = new InheritableThreadLocal<HttpServletRequest>();

    public static void set(HttpServletRequest httpServletRequest) {
        current.set(httpServletRequest);
    }

    public static HttpServletRequest get() {
        return current.get();
    }

    public static void cleanup(){
        current.remove();
    }
}
