package com.vaadin.cdi.shiro;

import javax.enterprise.inject.Alternative;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.vaadin.cdi.access.AccessControl;

/**
 * A simple access control implementation using Apache Shiro.
 * 
 * This implementation does not provide any custom Shiro session manager or
 * security context. To use server push and link Shiro sessions to Vaadin
 * session rather than the HTTP session, see e.g.
 * http://mikepilone.blogspot.fi/2013/07/vaadin-shiro-and-push.html .
 * 
 * In this test, Shiro is initialized by ShiroWebListener using shiro.ini and
 * sessions are managed using ShiroWebFilter.
 */
@Alternative
public class ShiroAccessControl extends AccessControl {

    @Override
    public boolean isUserSignedIn() {
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser.isAuthenticated();
    }

    @Override
    public boolean isUserInRole(String role) {
        // Note: in a real application, it might be preferable to check
        // permissions rather than roles here
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser.hasRole(role);
    }

    @Override
    public String getPrincipalName() {
        Subject currentUser = SecurityUtils.getSubject();
        Object principal = currentUser.getPrincipal();
        return (principal == null) ? null : String.valueOf(principal);
    }

}
