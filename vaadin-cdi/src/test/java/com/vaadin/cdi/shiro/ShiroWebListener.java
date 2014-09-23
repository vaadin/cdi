package com.vaadin.cdi.shiro;

import javax.servlet.annotation.WebListener;

import org.apache.shiro.web.env.EnvironmentLoaderListener;

/**
 * Initialize Shiro with shiro.ini on web application startup.
 * 
 * This subclass exists to register the listener with an annotation without
 * using web.xml .
 */
@WebListener
public class ShiroWebListener extends EnvironmentLoaderListener {

}
