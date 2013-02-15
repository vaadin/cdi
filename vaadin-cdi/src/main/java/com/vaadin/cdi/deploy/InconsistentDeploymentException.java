/*
 * Copyright 2012 Vaadin Ltd.
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
package com.vaadin.cdi.deploy;

/**
 * Thrown in case the contents of the deployment archive are not consistent:
 * <ol> <li>Multiple VaadinUIs are using the same path e.g.
 * <code>&#064;VaadinUI("a")</code>,
 * <code>&#064;VaadinUI("b")</code></li> <li>Several UIs annotated with
 * <code>&#064;Root</code> annotations are packaged</li> <li>A servlet defined
 * in the web.xml cannot be loaded.</li> </ol>
 */
public class InconsistentDeploymentException extends RuntimeException {

    public enum ID {

        MULTIPLE_ROOTS, PATH_COLLISION, CLASS_NOT_FOUND
    }
    private ID id;

    InconsistentDeploymentException(ID id, String message) {
        super(message);
        this.id = id;
    }

    InconsistentDeploymentException(ID id, Exception e) {
        super(e);
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "[" + id + "] Inconsistent deployment: " + getMessage();
    }
}
