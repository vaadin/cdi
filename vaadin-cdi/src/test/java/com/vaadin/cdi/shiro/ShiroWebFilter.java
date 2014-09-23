package com.vaadin.cdi.shiro;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;

import org.apache.shiro.web.servlet.ShiroFilter;

/**
 * Web filter to link Shiro authentication to the HTTP session. In a real
 * application, a custom session manager would typically be used - see
 * {@link ShiroTest}.
 * 
 * This subclass exists to register the filter with an annotation without using
 * web.xml .
 */
@WebFilter(filterName = "ShiroFilter", urlPatterns = { "/*" }, dispatcherTypes = {
        DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE,
        DispatcherType.ERROR })
public class ShiroWebFilter extends ShiroFilter {

}
