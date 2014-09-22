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
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 * Instances of this class are used as an identifier for determining 
 * the correct ContextualStorage in UIScopedContext
 * 
 *
 */
public class UIContextual implements Contextual {
    
    protected Contextual delegate;
    protected int uiId;
    protected long sessionId;
    
    public UIContextual(Contextual delegate, long sessionId, int uiId) {
        this.delegate = delegate;
        this.uiId = uiId;
        this.sessionId = sessionId;
    }

    public int getUiId() {
        return uiId;
    }

    public long getSessionId() {
        return sessionId;
    }
    
    @Override
    public Object create(CreationalContext context) {
        return delegate.create(context);
    }

    @Override
    public void destroy(Object instance, CreationalContext context) {
        delegate.destroy(instance, context);        
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof UIContextual)) {
            return false;
        }
        
        UIContextual uiContextual = (UIContextual) o;

        if (uiId != uiContextual.uiId)
            return false;
        if (sessionId != uiContextual.sessionId)
            return false;

        return true;

    }
    
    @Override
    public int hashCode() {
        int result = (int) sessionId;
        result = 31 * result + uiId;
        return result;
    }

}
