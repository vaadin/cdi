/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
