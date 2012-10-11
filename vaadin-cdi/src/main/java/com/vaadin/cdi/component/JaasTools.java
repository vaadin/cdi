package com.vaadin.cdi.component;

import java.security.Principal;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServletService;

/**
 * JaasTools provides set of JAAS helper methods to login and out the user as
 * well as to query role information. In order to use JaasTools proper security
 * domain must be configured to underlying application server.
 */
public class JaasTools {

    /**
     * Logs in the user to underlying container security context using
     * configured security domain in deployment descriptor
     * 
     * @param username
     * @param password
     * @throws ServletException
     *             if login fails or current session has already been
     *             authenticated
     */
    public static void login(String username, String password)
            throws ServletException {
        getCurrentRequest().login(username, password);
    }

    /**
     * Logs user out from current container managed security context
     * 
     * @throws ServletException
     */
    public static void logout() throws ServletException {
        getCurrentRequest().logout();
    }

    /**
     * @return true if user has been signed in to underlying container's
     *         security domain
     */
    public static boolean isUserSignedIn() {
        Principal principal = getCurrentRequest().getUserPrincipal();
        return principal != null;
    }

    /**
     * @param role
     * @return true if curently logged in user is in given role.
     */
    public static boolean isUserInRole(String role) {
        return getCurrentRequest().isUserInRole(role);
    }

    /**
     * @return name that was used to login user
     */
    public static String getPrincipalName() {
        return getCurrentRequest().getUserPrincipal().getName();
    }

    /**
     * @param roles
     * @return true if currently logged in user is in some of given roles
     */
    public static boolean isUserInSomeRole(String... roles) {
        for (String role : roles) {
            if (isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }

    @Produces
    @RequestScoped
    public static HttpServletRequest getCurrentRequest() {
        HttpServletRequest request = VaadinServletService
                .getCurrentServletRequest();
        getLogger().info("Getting request " + request);

        return request;
    }

    private static Logger getLogger() {
        return Logger.getLogger(JaasTools.class.getCanonicalName());
    }
}
