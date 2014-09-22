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

import javax.enterprise.context.spi.Contextual;

import com.vaadin.ui.UI;

/**
 * Instances of this class are used as an identifier for determining 
 * the correct ContextualStorage in ViewScopedContext
 * 
 *
 */
public class ViewContextual extends UIContextual {

    /**
     * The mapped name of the view this ViewContextual is associated with.
     * 
     * Must be a non-null value.
     */
    protected String viewIdentifier;
    
    public ViewContextual(Contextual delegate, long sessionId, int uiId, String viewIdentifier) {
        super(delegate, sessionId, uiId);
        this.viewIdentifier = viewIdentifier;
    }
    
    public ViewContextual(Contextual delegate, long sessionId, String viewIdentifier) {
        this(delegate, sessionId, UI.getCurrent().getUIId(), viewIdentifier);
    }
    
    public ViewContextual(Contextual delegate, String viewIdentifier) {
        this(delegate, CDIUtil.getSessionId(), viewIdentifier);
    }
    
    @Override
    public boolean equals(Object o) {
        ViewContextual vc = null;
        if (o instanceof ViewContextual) {
            vc = (ViewContextual) o;
            return super.equals(o)
                    && this.viewIdentifier.equals(vc.viewIdentifier);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = (int) sessionId;
        result = (31 * result + uiId) ^ viewIdentifier.hashCode();
        return result;
    }

}
