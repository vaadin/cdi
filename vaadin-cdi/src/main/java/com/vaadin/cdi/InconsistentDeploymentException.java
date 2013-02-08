package com.vaadin.cdi;

/**
 * @author: adam-bien.com
 */
public class InconsistentDeploymentException extends RuntimeException {
    public InconsistentDeploymentException(String message) {
        super(message);
    }

    public InconsistentDeploymentException(Exception e) {
        super(e);
    }
}
