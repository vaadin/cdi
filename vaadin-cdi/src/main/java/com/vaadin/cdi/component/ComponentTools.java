package com.vaadin.cdi.component;

import com.vaadin.ui.Component;
import javax.enterprise.context.RequestScoped;

/**
 * 
 * ComponentTools is JaasTools addition that allows enabling/disabling, and
 * hiding Vaadin component based on roles available through JAAS. In order to
 * use these methods user must be signed in to container managed security
 * domain.
 */
public class ComponentTools {

    /**
     * Sets given component enabled if currently signed in user is in one or
     * more given roles, otherwise component is disabled.
     * 
     * @param component
     * @param roles
     */
    public void setEnabledForRoles(Component component, String... roles) {
        component.setEnabled(JaasTools.isUserInSomeRole(roles));
    }

    /**
     * Sets given component visible if currently signed in user is in one or
     * more given roles, otherwise component is hidden.
     * 
     * @param component
     * @param roles
     */
    public void setVisibleForRoles(Component component, String... roles) {
        component.setVisible(JaasTools.isUserInSomeRole(roles));
    }
}
