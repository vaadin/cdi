package com.vaadin.reports.business.issues.boundary;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.vaadin.reports.business.issues.control.BugStore;

/**
 * 
 * @author adam-bien.com
 */
@Stateless
public class BugTracking {

    @Inject
    BugStore bs;

    public void fileBug(String bug) {
        bs.storeBug(bug);
    }
}
