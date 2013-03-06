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
package com.vaadin.cdi.internal;

/**
 * Thrown in case the contents of the deployment archive are not consistent:
 * <ol>
 *     <li>Multiple VaadinUIs are using the same path e.g. @VaadinUI("a"), @VaadinUI("b")</li>
 *     <li>Several UIs annotated with @Root annotations are packaged</li>
 *     <li>A servlet defined in the web.xml cannot be loaded.</li>
 * </ol>
 * @author: adam-bien.com
 */
public class InconsistentDeploymentException extends RuntimeException {

    enum ID{ MULTIPLE_ROOTS, PATH_COLLISION,CLASS_NOT_FOUND}

    private ID id;

    public InconsistentDeploymentException(ID id,String message) {
        super(message);
        this.id = id;
    }

    public InconsistentDeploymentException(ID id,Exception e) {
        super(e);
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + id + "] Inconsistent deployment: " + getMessage();
    }
}
