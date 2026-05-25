/*
 * Vaadin CDI Add-on
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.internal;

/**
 * Thrown in case the contents of the deployment archive are not consistent:
 * <ol>
 * <li>Multiple VaadinUIs are using the same path e.g. @CDIUI("a"), @CDIUI("b")</li>
 * <li>Several UIs annotated with @CDIUI annotations with an empty context path
 * are packaged</li>
 * <li>A servlet defined in the web.xml cannot be loaded.</li>
 * <li>A nested servlet class of an inappropriate type is defined in a @CDIUI
 * class.</li>
 * <li>A @CDIView does not implement View.</li>
 * </ol>
 */
public class InconsistentDeploymentException extends RuntimeException {

    enum ID {
        MULTIPLE_ROOTS,
        PATH_COLLISION,
        CLASS_NOT_FOUND,
        EMBEDDED_SERVLET,
        CDIVIEW_WITHOUT_VIEW,
        CDIVIEW_DEPENDENT,
        CDIUI_SCOPE,
        CDIUI_WITHOUT_UI
    }

    private ID id;

    public InconsistentDeploymentException(ID id, String message) {
        super(message);
        this.id = id;
    }

    public InconsistentDeploymentException(ID id, Exception e) {
        super(e);
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + id + "] Inconsistent deployment: " + getMessage();
    }
}
