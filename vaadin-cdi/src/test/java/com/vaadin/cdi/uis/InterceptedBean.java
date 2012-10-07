package com.vaadin.cdi.uis;

import javax.interceptor.Interceptors;

/**
 * @author: adam-bien.com
 */
@Interceptors(InstrumentedInterceptor.class)
public class InterceptedBean {

    public String fromInterceptorBean(){
        return "hello from intercepted bean";
    }
}
