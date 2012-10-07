package com.vaadin.cdi.uis;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: adam-bien.com
 */
public class InstrumentedInterceptor {

    private final static AtomicInteger INTERCEPTION_COUNTER = new AtomicInteger(0);


    @AroundInvoke
    public Object intercept(InvocationContext invocationContext) throws Exception{
        System.out.println("---invoked: " + invocationContext.getMethod());
        INTERCEPTION_COUNTER.incrementAndGet();
        return invocationContext.proceed();

    }

    public static int getCounter(){
        return INTERCEPTION_COUNTER.get();
    }

    public static void reset(){
        INTERCEPTION_COUNTER.set(0);
    }
}
